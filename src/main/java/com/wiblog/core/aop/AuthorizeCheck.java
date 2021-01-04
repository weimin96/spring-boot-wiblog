package com.wiblog.core.aop;

import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.weixin.MsgEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * @author pwm
 * @date 2019/10/11
 */
@Retention(RetentionPolicy.RUNTIME)//运行时去动态获取注解信息
@Target(ElementType.METHOD)
public @interface AuthorizeCheck {

    /**
     * 角色等级 1对应系统管理员 2对应普通管理员 3普通用户
     */
    RoleEnum grade() default RoleEnum.USER;

}
