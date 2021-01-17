package com.wiblog.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiblog.core.entity.Article;
import com.wiblog.core.vo.ArticleDetailVo;
import com.wiblog.core.vo.ArticlePageVo;
import com.wiblog.core.vo.ArticleVo;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Mapper 接口
 *
 * @author pwm
 * @since 2019-06-12
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取文章分页列表
     *
     * @param page       page
     * @param state      私密
     * @param categoryId 文章分类
     * @return IPage
     */
    IPage<ArticlePageVo> selectPageList(Page<Article> page,
                                        @Param(value = "state") Integer state,
                                        @Param(value = "categoryId") Long categoryId);

    /**
     * 获取文章详细信息
     *
     * @param id id
     * @return ArticleVo
     */
    ArticleVo selectArticleById(Long id);

    /**
     * 文章页获取文章所有信息
     *
     * @param url url
     * @return ArticleDetailVo
     */
    ArticleDetailVo selectArticleByUrl(String url);

    /**
     * 修改文章分类为未分类
     *
     * @param id id
     * @return int
     */
    int updateByCategoryId(Long id);

    /**
     * 修改文章状态为0
     *
     * @param id id
     * @return int
     */
    int updateState(Long id);

    /**
     * 批量更新点击率
     *
     * @param list list
     * @return int
     */
    int updateHitsBatch(List list);

    /**
     * 通过id队列查找文章列表
     *
     * @param list list
     * @return List
     */
    List<Article> selectArticleByIds(List list);

    /**
     * 批量更新点赞
     *
     * @param likeList likeList
     * @return int
     */
    int updateLikesBatch(List likeList);

    /**
     * 获取点击数和文章id
     *
     * @return List
     */
    List<Map<String, Object>> selectHits();

    /**
     * 获取点赞数和文章id
     *
     * @return List
     */
    List<Map<String, Object>> selectLikes();

    List<Map> selectListForTitleAndUrl();

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
