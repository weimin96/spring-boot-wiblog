package com.wiblog.core.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/8
 */
@Data
public class UserVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long uid;


    /**
     * 用户名
     */
    private String username;

    /**
     * 性别 male or female
     */
    private String sex;

    private String email;

    private String phone;

    private List<String> identityTypes;

    /**
     * 头像地址
     */
    private String avatarImg;

    /**
     * 状态 0删除
     */
    private Boolean state;

    /**
     * 创建时间
     */
    private Date createTime;
}
