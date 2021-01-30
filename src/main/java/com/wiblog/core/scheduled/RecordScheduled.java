package com.wiblog.core.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.Constant;
import com.wiblog.core.entity.Article;
import com.wiblog.core.service.IArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞-点击率定时器
 *
 * @author pwm
 * @date 2019/11/15
 */
@Slf4j
@Component
@EnableScheduling
@PropertySource(value = "classpath:/wiblog.properties", encoding = "utf-8")
public class RecordScheduled {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${host-url}")
    private String hostUrl;

    @Autowired
    private IArticleService articleService;

    /**
     * 定时更新 article_detail es 排行榜
     * <p>
     * // 1.数据显示 直接返回数据库数据
     * // 2.点击-缓存加一
     * // 3.定时更新数据库 缓存置0
     * // 定时更新排行
     */
//    @Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void recordHit() {
        System.out.println("定时任务");
        // 点击率 初始化
        //Map<String, Integer> hitMap = new HashMap<>(16);
        // 点赞 初始化
        //Map<String, Integer> likeMap = new HashMap<>(16);
        // 文章详细信息
        Map<String, Object> articleDetail = new HashMap<>(16);

        // 获取 点击率
//        Map<Object, Object> hitCache = redisTemplate.opsForHash().entries(Constant.RedisKey.HIT_RECORD_KEY);
        // 获取 点赞数据
//        Map<Object, Object> likeCache = redisTemplate.opsForHash().entries(Constant.RedisKey.LIKE_RECORD_KEY);

        LambdaQueryWrapper<Article> queryWrapper = new QueryWrapper<Article>().lambda();
        queryWrapper.select(Article::getId, Article::getTitle, Article::getArticleUrl, Article::getHits, Article::getLikes).eq(Article::getState, "1").eq(Article::getPrivately, "0");
        List<Article> articleList = articleService.list(queryWrapper);
        // 合并数据
        articleList.forEach(it -> {
            String id = String.valueOf(it.getId());
            int hit = it.getHits();
//            Integer like = (Integer) likeCache.get(id) + it.getLikes();
//            it.setHits(hit);
//            it.setLikes(like);

            // 更新排行榜
            redisTemplate.opsForZSet().add(Constant.RedisKey.ARTICLE_RANKING_KEY, id, hit);
            // 初始化数据
            //hitMap.put(id, 0);
            //likeMap.put(id, 0);

            articleDetail.put(id, it);
        });

        redisTemplate.delete(Constant.RedisKey.ARTICLE_DETAIL_KEY);
        redisTemplate.opsForHash().putAll(Constant.RedisKey.ARTICLE_DETAIL_KEY, articleDetail);
        // 点击率 初始化
        //redisTemplate.opsForHash().putAll(Constant.RedisKey.HIT_RECORD_KEY, hitMap);
        // 点赞 初始化
        //redisTemplate.opsForHash().putAll(Constant.RedisKey.LIKE_RECORD_KEY, likeMap);

        // 数据库更新
//        if (articleList.size() > 0) {
//            articleService.updateHitsBatch(articleList);
//            articleService.updateLikesBatch(articleList);
//        }

    }

    /**
     * 定时提交链接给百度站长
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void pushArticle() {
        String api_url = "http://data.zz.baidu.com/urls?site=www.wiblog.cn&token=OesFmLmaNBZXO2G1";
        List<Article> list = articleService.list(new QueryWrapper<Article>().eq("state", "1"));
        StringBuilder builder = new StringBuilder();
        for (Article article : list) {
            String url = hostUrl + article.getArticleUrl() + "\n";
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
        if (result != null && result.get("message") != null) {
            log.error("主动更新异常{}", (String) result.get("message"));
        }
    }
}
