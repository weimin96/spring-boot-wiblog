package com.wiblog.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 *
 * @author pwm
 * @date 2019-11-07
 */
@Data
public class UserAuth implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;


    private Long uid;

    /**
     * 登录类型 phone|email|username|github
     */
    private String identityType;

    /**
     * 标识（手机号|邮箱|用户名|第三方唯一标识）
     */
    private String identifier;

    /**
     * 密码|token
     */
    @JsonIgnore
    private String password;

    /**
     * 状态 0删除
     */
    private Boolean state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上次登录时间
     */
    private Date logged;

}
