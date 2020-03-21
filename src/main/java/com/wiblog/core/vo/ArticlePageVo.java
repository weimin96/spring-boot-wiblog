package com.wiblog.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author pwm
 * @date 2019-06-12
 */
@Data
public class ArticlePageVo implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 作者id
     */
    private Long uid;


    /**
     * 作者名
     */
    private String author;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String articleSummary;;

    /**
     * 标签
     */
    private String tags;

    /**
     * 分类url
     */
    private String categoryUrl;

    /**
     * 分类
     */
    private String categoryName;

    /**
     * 文章封面
     */
    private String imgUrl;

    /**
     * 文章地址
     */
    private String articleUrl;

    /**
     * 喜欢
     */
    private Integer likes;

    /**
     * 点击量
     */
    private Integer hits;

    /**
     * 是否设为私密 1私密
     */
    private Boolean privately;

    /**
     * 是否开放打赏 1开启
     */
    private Boolean reward;

    /**
     * 是否开放评论 1开放
     */
    private Boolean comment;

    /**
     * 评论数
     */
    private Integer commentsCounts;

    private Date createTime;

}
