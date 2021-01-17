package com.wiblog.core.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.Constant;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.EsArticle;
import com.wiblog.core.elastic.EsArticleRepository;
import com.wiblog.core.service.IArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class CacheInit {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private EsArticleRepository esArticleRepository;

    /**
     * 初始化redis数据
     */
    @PostConstruct
    public void initRedisCache() {
        // 查找文章点击率 点赞 url id 标题
        String isInit = (String) redisTemplate.opsForValue().get(Constant.RedisKey.APP_INIT);
        if (StringUtils.isBlank(isInit)) {
            log.info("初始化缓存");
            LambdaQueryWrapper<Article> queryWrapper = new QueryWrapper<Article>().lambda();
            queryWrapper.select(Article::getId, Article::getTitle, Article::getArticleUrl, Article::getHits, Article::getLikes).eq(Article::getState, "1").eq(Article::getPrivately, "0");
            List<Article> articleList = articleService.list(queryWrapper);
            // 点击率 初始化
            //Map<String, Integer> hitMap = new HashMap<>(16);
            // 点赞 初始化
            //Map<String, Integer> likeMap = new HashMap<>(16);
            // 排行榜
            Set<ZSetOperations.TypedTuple<Object>> rankSet = new HashSet<>();
            Map<String, Object> articleDetail = new HashMap<>(16);
            articleList.forEach(it -> {
                String id = String.valueOf(it.getId());
                //hitMap.put(id, 0);
                //likeMap.put(id, 0);
                // 文章排行榜
                ZSetOperations.TypedTuple<Object> tuple = new DefaultTypedTuple<>(id, (double) it.getHits());
                rankSet.add(tuple);

                articleDetail.put(id, it);
            });
            // 点击率 初始化
            //redisTemplate.opsForHash().putAll(Constant.RedisKey.HIT_RECORD_KEY, hitMap);
            // 点赞 初始化
            //redisTemplate.opsForHash().putAll(Constant.RedisKey.LIKE_RECORD_KEY, likeMap);
            // 排行榜
            redisTemplate.opsForZSet().add(Constant.RedisKey.ARTICLE_RANKING_KEY, rankSet);

            redisTemplate.opsForHash().putAll(Constant.RedisKey.ARTICLE_DETAIL_KEY, articleDetail);
            redisTemplate.opsForValue().set(Constant.RedisKey.APP_INIT, "true");
        }
    }

    /**
     * 初始化elastic search数据
     */
    @PostConstruct
    public void initElasticData() {
        String isInit = (String) redisTemplate.opsForValue().get(Constant.RedisKey.ELASTIC_SEARCH_INIT);
        if (StringUtils.isBlank(isInit)) {
            log.info("初始化elastic search");
            esArticleRepository.deleteAll();
            LambdaQueryWrapper<Article> queryWrapper = new QueryWrapper<Article>().lambda();
            queryWrapper.eq(Article::getState, "1").eq(Article::getPrivately, "0");
            List<Article> articleList = articleService.list(queryWrapper);
            List<EsArticle> esArticleList = new ArrayList<>();
            articleList.forEach(it -> {
                EsArticle esArticle = new EsArticle();
                esArticle.setArticleId(it.getId());
                esArticle.setCategoryId(it.getCategoryId());
                esArticle.setContent(it.getContent());
                esArticle.setTitle(it.getTitle());
                esArticle.setUrl(it.getArticleUrl());
                esArticleList.add(esArticle);
            });
            esArticleRepository.saveAll(esArticleList);
            redisTemplate.opsForValue().set(Constant.RedisKey.ELASTIC_SEARCH_INIT, "true");
        }
    }
}
