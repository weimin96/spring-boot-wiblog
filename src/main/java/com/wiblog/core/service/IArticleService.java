package com.wiblog.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.User;
import com.wiblog.core.vo.ArticleDetailVo;
import com.wiblog.core.vo.ArticlePageVo;

import java.util.List;

/**
 * 服务类
 *
 * @author pwm
 * @since 2019-06-12
 */
public interface IArticleService extends IService<Article> {

    /**
     * 获取文章列表
     *
     * @param pageNum    pageNum
     * @param pageSize   pageSize
     * @param categoryId 文章分类
     * @return ServerResponse
     */
    ServerResponse<IPage<ArticlePageVo>> articlePageList(Integer pageNum, Integer pageSize, Long categoryId);

    /**
     * 通过url获取文章信息
     *
     * @param url  url
     * @param user user
     * @return ServerResponse
     */
    ServerResponse<ArticleDetailVo> getArticle(String url, User user);

    /**
     * 获取文章管理列表
     *
     * @param num  num
     * @param size size
     * @return ServerResponse
     */
    ServerResponse<IPage<ArticlePageVo>> articlesManage(Integer num, Integer size);

    /**
     * 删除文章
     *
     * @param id id
     * @return ServerResponse
     */
    ServerResponse<?> delArticle(Long id);

    /**
     * 文章排行榜
     *
     * @return ServerResponse
     */
    ServerResponse<?> getArticleRank();

    /**
     * 批量更新点击数
     * @param articleList articleList
     */
    void updateHitsBatch(List<Article> articleList);

    /**
     * 批量更新点赞数
     * @param articleList articleList
     */
    void updateLikesBatch(List<Article> articleList);

    /**
     * 更新文章点赞数
     * @param articleId articleId
     */
    void updateLikesById(Long articleId);

    /**
     * 更新文章点击数
     * @param articleId articleId
     */
    void updateHitsById(Long articleId);
}
