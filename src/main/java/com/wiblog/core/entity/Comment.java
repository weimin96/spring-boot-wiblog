package com.wiblog.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author pwm
 * @date 2019-09-01
 */
@Data
public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;


    /**
     * 用户id
     */
    private Long uid;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 父评论id 0为评论文章
     */
    private Long parentId;

    /**
     * 主评论id 0为评论文章
     */
    private Long genId;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态 0删除
     */
    private Boolean state;

    private Date createTime;

    private Date updateTime;

}
