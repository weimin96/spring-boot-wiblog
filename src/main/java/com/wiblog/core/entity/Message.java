package com.wiblog.core.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 *
 * @author pwm
 * @date 2019-11-06
 */
@Data
public class Message implements Serializable{

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
     * 通知分类id 1公告 2评论 3点赞 4系统通知
     */
    private Integer type;

    /**
     * 内容
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 状态 0已读 1未读
     */
    private Boolean state;

    private Date createTime;

    private Date updateTime;

}
