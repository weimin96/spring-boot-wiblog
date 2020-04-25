package com.wiblog.core.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.Constant;
import com.wiblog.core.entity.Article;
import com.wiblog.core.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 点赞-点击率定时器
 *
 * @author pwm
 * @date 2019/11/15
 */
@Slf4j
@Component
@EnableScheduling
public class RecordScheduled {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Scheduled(cron = "0 0 */2 * * ?")
    public void recordHit(){
        // 获取 点击率存入数据库
        Map<Object,Object> hitMap = redisTemplate.opsForHash().entries(Constant.HIT_RECORD_KEY);
        // 缓存不存在 将数据库数据导入缓存
        if (hitMap == null || hitMap.isEmpty()){
            List<Map> hitList = articleMapper.selectHits();
            Map<String, Object> hits = new HashMap<>(16);
            for (Map item:hitList){
                hits.put(String.valueOf(item.get("id")),item.get("hits"));
                // 更新排行榜
                redisTemplate.opsForZSet().add(Constant.ARTICLE_RANKING_KEY, item.get("id"), Double.parseDouble(item.get("hits").toString()));
            }
            redisTemplate.opsForHash().putAll(Constant.HIT_RECORD_KEY,hits);
        }else {
            Iterator<Map.Entry<Object, Object>> it = hitMap.entrySet().iterator();
            List<Map> dataList = new ArrayList<>();
            while (it.hasNext()) {
                Map.Entry<Object, Object> itData = it.next();
                Map<String, Object> data = new HashMap<>(2);
                data.put("id", itData.getKey());
                data.put("hits", itData.getValue());
                dataList.add(data);
                // 文章排行榜
                redisTemplate.opsForZSet().add(Constant.ARTICLE_RANKING_KEY, itData.getKey(), Double.parseDouble(itData.getValue().toString()));

            }
            if (dataList.size() > 0) {
                articleMapper.updateHitsBatch(dataList);
            }
        }

        // 点赞存入数据库
        Map<Object,Object> likeMap = redisTemplate.opsForHash().entries(Constant.LIKE_RECORD_KEY);
        // 缓存不存在 将数据库数据导入缓存
        if (likeMap == null || likeMap.isEmpty()){
            List<Map> likeList = articleMapper.selectLikes();
            Map<String, Object> likes = new HashMap<>(16);
            for (Map item:likeList){
                likes.put(String.valueOf(item.get("id")),item.get("hits"));
            }
            redisTemplate.opsForHash().putAll(Constant.LIKE_RECORD_KEY,likes);
        }else {
            Iterator<Map.Entry<Object, Object>> itLike = likeMap.entrySet().iterator();
            List<Map> likeList = new ArrayList<>();
            while (itLike.hasNext()) {
                Map.Entry<Object, Object> itData = itLike.next();
                Map<String, Object> data = new HashMap<>(2);
                data.put("id", itData.getKey());
                data.put("likes", itData.getValue() == null ? 0 : itData.getValue());
                likeList.add(data);
            }
            if (likeList.size() > 0) {
                articleMapper.updateLikesBatch(likeList);
            }
        }
        // 更新文章信息
        redisTemplate.delete(Constant.ARTICLE_DETAIL_KEY);
        List<Map> list = articleMapper.selectListForTitleAndUrl();
        for (Map article:list){
            redisTemplate.opsForHash().put(Constant.ARTICLE_DETAIL_KEY,String.valueOf(article.get("id")),article);
        }

    }

    @Scheduled(cron = "0 0 */2 * * ?")
    public void pushArticle(){
        String api_url = "http://data.zz.baidu.com/urls?site=www.wiblog.cn&token=OesFmLmaNBZXO2G1";
        List<Article> list = articleMapper.selectList(new QueryWrapper<Article>().eq("state","1"));
        StringBuilder builder = new StringBuilder();
        for (Article article : list) {
            String url = "https://www.wiblog.cn" + article.getArticleUrl()+"\n";
            builder.append(url);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "data.zz.baidu.com");
        headers.add("User-Agent", "curl/7.12.1");
        headers.add("Content-Length", "83");
        headers.add("Content-Type", "text/plain");
        HttpEntity<String> entity = new HttpEntity<String>(builder.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.postForObject(api_url, entity, JSONObject.class);
        if (result!=null && result.get("message")!=null){
            log.error("主动更新异常{}",(String) result.get("message"));
        }
    }
}
