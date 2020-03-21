package com.wiblog.core.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/9/1
 */
@Data
public class SubCommentVo implements Serializable{

    private static final long serialVersionUID = -4046504102997611516L;

    /**
     * 主键id
     */
    private Long id;


    /**
     * 用户id
     */
    private Long uid;

    /**
     * 用户id
     */
    private Long replyedId;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 主评论id 0为评论文章
     */
    private Long genId;

    /**
     * 父评论id 0为评论文章
     */
    private Long parentId;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态
     */
    private Integer state;

    private Date createTime;

    private Date updateTime;

    /**
     * 用户名
     */
    private String replyName;

    /**
     * 用户名
     */
    private String replyedName;

    /**
     * 用户头像
     */
    private String replyAvatarImg;

    /**
     * 用户头像
     */
    private String replyedAvatarImg;


}
