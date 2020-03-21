package com.wiblog.core.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 *
 * @author pwm
 * @date 2019-10-09
 */
@Data
public class UserRole implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    private Long uid;

    private Long roleId;

}
