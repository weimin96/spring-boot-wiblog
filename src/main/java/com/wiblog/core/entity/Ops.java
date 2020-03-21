package com.wiblog.core.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 *
 * @author pwm
 * @date 2019-10-24
 */
@Data
public class Ops implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;


    /**
     * 用户名
     */
    private String username;

    /**
     * 详情
     */
    private String msg;

    private Date createTime;

    private Date updateTime;

}
