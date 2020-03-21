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
public class UserCommentVo implements Serializable{

    private static final long serialVersionUID = -4046504102997611516L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论内容
     */
    private String parentContent;

    /**
     * 父评论状态
     */
    private Integer state;

    private Date createTime;

    /**
     * 用户名
     */
    private String username;

    /**
     * 文章
     */
    private String articleUrl;

    /**
     * 文章
     */
    private String title;

}
