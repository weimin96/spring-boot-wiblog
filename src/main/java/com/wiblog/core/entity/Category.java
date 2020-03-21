package com.wiblog.core.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 *
 * @author pwm
 * @date 2019-06-15
 */
@Data
public class Category implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 上级id
     */
    private Long parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 同级排序
     */
    private Integer rank;

}
