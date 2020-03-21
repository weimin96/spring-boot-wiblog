package com.wiblog.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author pwm
 * @date 2019-10-16
 */
@Getter
@Setter
public class Picture implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;


    /**
     * 文件名
     */
    private String name;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件链接
     */
    private String url;

    /**
     * 额外字段
     */
    private String extra;

    private Date createTime;

    private Date updateTime;

    public Picture() {
    }

    public Picture(Long id, String name, String type, String url, String extra, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.extra = extra;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Picture(String name, String type, String url, String extra, Date createTime, Date updateTime) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.extra = extra;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
