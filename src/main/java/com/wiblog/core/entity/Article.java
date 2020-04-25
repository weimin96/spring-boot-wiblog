package com.wiblog.core.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author pwm
 * @date 2019-06-12
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class Article implements Serializable{

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
     * 内容
     */
    private String content;

    /**
     * 标签
     */
    private String tags;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 文章封面
     */
    private String imgUrl;

    /**
     * 文章地址
     */
    private String articleUrl;

    /**
     * 简介
     */
    private String articleSummary;

    /**
     * 点击量
     */
    private Integer hits;

    /**
     * 点赞
     */
    private Integer likes;

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
     * 是否删除 0删除
     */
    private Boolean state;

    private Date createTime;

    private Date updateTime;

}
