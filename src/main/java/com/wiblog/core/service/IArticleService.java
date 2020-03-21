package com.wiblog.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.User;
import com.wiblog.core.vo.ArticleDetailVo;

import java.util.Set;

/**
 *   服务类
 *
 * @author pwm
 * @since 2019-06-12
 */
public interface IArticleService extends IService<Article> {

    /**
     * 获取所有文章列表
     * @return ServerResponse
     */
    ServerResponse getAllArticle();

    /**
     * 获取文章列表
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return ServerResponse
     */
    ServerResponse<IPage> articlePageList(Integer pageNum, Integer pageSize);

    /**
     * 获取文章详细信息
     * @param id id
     * @return ServerResponse
     */
    ServerResponse getArticleById(Long id);

    /**
     * 通过url获取文章信息
     * @param url url
     * @param user user
     * @return ServerResponse
     */
    ServerResponse<ArticleDetailVo> getArticle(String url, User user);

    /**
     * 获取文章管理列表
     * @param num num
     * @param size size
     * @return ServerResponse
     */
    ServerResponse<IPage> articlesManage(Integer num, Integer size);

    /**
     * 删除文章
     * @param id id
     * @return ServerResponse
     */
    ServerResponse delArticle(Long id);

    /**
     * 文章排行榜
     * @param rankSet rankSet
     * @return ServerResponse
     */
    ServerResponse getArticleRank(Set<Object> rankSet);
}
