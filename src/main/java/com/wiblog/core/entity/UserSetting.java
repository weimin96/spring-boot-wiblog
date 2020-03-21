package com.wiblog.core.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 *
 * @author pwm
 * @date 2019-11-17
 */
@Data
public class UserSetting implements Serializable{

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
     * 是否开放评论
     */
    private Integer comment;

    /**
     * 是否开放收藏
     */
    private Integer star;

}
