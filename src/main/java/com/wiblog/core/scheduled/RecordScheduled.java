package com.wiblog.core.scheduled;

import com.wiblog.core.common.Constant;
import com.wiblog.core.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        Iterator<Map.Entry<Object, Object>> it = hitMap.entrySet().iterator();
        List<Map> dataList = new ArrayList<>();
        while (it.hasNext()){
            Map.Entry<Object, Object> itData = it.next();
            Map<String,Object> data = new HashMap<>(2);
            data.put("id",itData.getKey());
            data.put("hits",itData.getValue());
            dataList.add(data);
            // 文章排行榜
            redisTemplate.opsForZSet().add(Constant.ARTICLE_RANKING_KEY,itData.getKey(),Double.parseDouble(itData.getValue().toString()));
        }
        if (dataList.size()>0) {
            articleMapper.updateHitsBatch(dataList);
        }

        // 点赞存入数据库
        Map<Object,Object> likeMap = redisTemplate.opsForHash().entries(Constant.LIKE_RECORD_KEY);
        Iterator<Map.Entry<Object, Object>> itLike = likeMap.entrySet().iterator();
        List<Map> likeList = new ArrayList<>();
        while (itLike.hasNext()){
            Map.Entry<Object, Object> itData = itLike.next();
            Map<String,Object> data = new HashMap<>(2);
            data.put("id",itData.getKey());
            data.put("likes",itData.getValue() == null ? 0:itData.getValue());
            likeList.add(data);
        }
        if(likeList.size()>0) {
            articleMapper.updateLikesBatch(likeList);
        }
    }

}
