package com.wiblog.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/9/1
 */
@Data
public class CommentManageVo implements Serializable{

    private static final long serialVersionUID = -4046504102997611516L;

    /**
     * 评论id
     */
    private Long id;


    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 点赞数量
     */
    private Integer likes;

    /**
     * 回复数量
     */
    private Integer replyNum;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态
     */
    private Integer state;

    private Date createTime;

    /**
     * 用户名
     */
    private String username;
}
