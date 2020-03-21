package com.wiblog.core.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author pwm
 * @date 2019-06-01
 */
@Data
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private Long uid;


    /**
     * 用户名
     */
    private String username;

    /**
     * 性别 male or female
     */
    private String sex;

    /**
     * 头像地址
     */
    private String avatarImg;

    /**
     * 介绍
     */
    private String intro;

    /**
     * 省
     */
    private String region;

    /**
     * 市
     */
    private String city;

    /**
     * 状态 0删除
     */
    private Boolean state;

    /**
     * 创建时间
     */
    private Date createTime;

}
