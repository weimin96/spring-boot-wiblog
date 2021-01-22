package com.wiblog.core.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.wiblog.core.elastic.EsArticleRepository;
import com.wiblog.core.entity.EsArticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监听article表的变化，更新elasticsearch
 *
 * @author pwm
 * @date 2021/1/22
 */
@Slf4j
@Component
public class ArticleListener {

    private static final String TABLE_NAME = "myblog.article";

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${ip}")
    private String ip;

    @Value("${datasource.port}")
    private int port;

    @Autowired
    EsArticleRepository articleRepository;

    @PostConstruct
    public void onListener() throws IOException {

        // 获取监听数据表数组
        List<String> databaseList = Collections.singletonList(TABLE_NAME);
        Map<Long, String> tableMap = new HashMap<>(8);
        //构造BinaryLogClient，填充mysql链接信息
        BinaryLogClient client = new BinaryLogClient(ip, port, username, password);
        client.setServerId(1);

        client.registerEventListener(event -> {
            EventData data = event.getData();
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                tableMap.put(tableMapEventData.getTableId(), tableMapEventData.getDatabase() + "." + tableMapEventData.getTable());
            }
            if (data instanceof UpdateRowsEventData) {
                UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
                String tableName = tableMap.get(updateRowsEventData.getTableId());
                if (tableName != null && databaseList.contains(tableName)) {
                    log.info("--------Update Article-----------");
                    for (Map.Entry<Serializable[], Serializable[]> row : updateRowsEventData.getRows()) {
                        // 修改后的值
                        Serializable[] key = row.getValue();
                        EsArticle esArticle = articleRepository.queryEsArticleByArticleId((Long) key[0]);
                        esArticle.setTitle((String) key[3])
                                .setContent(new String((byte[]) key[4]))
                                .setCategoryId((Long) key[6])
                                .setUrl((String) key[8]);
                        articleRepository.save(esArticle);
                    }
                }
            } else if (data instanceof WriteRowsEventData) {
                WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
                String tableName = tableMap.get(writeRowsEventData.getTableId());
                if (tableName != null && databaseList.contains(tableName)) {
                    log.info("--------Insert Article-----------");
                    for (Serializable[] key : writeRowsEventData.getRows()) {
                        EsArticle esArticle = new EsArticle();
                        esArticle.setArticleId((Long) key[0])
                                .setTitle((String) key[3])
                                .setContent(new String((byte[]) key[4]))
                                .setCategoryId((Long) key[6])
                                .setUrl((String) key[8]);
                        articleRepository.save(esArticle);
                    }
                }
            } else if (data instanceof DeleteRowsEventData) {
                DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
                String tableName = tableMap.get(deleteRowsEventData.getTableId());
                if (tableName != null && databaseList.contains(tableName)) {
                    log.info("--------Delete Article-----------");
                    for (Serializable[] key : deleteRowsEventData.getRows()) {
                        articleRepository.deleteByArticleId((Long) key[0]);
                    }
                }
            }
        });

        client.connect();

    }
}
