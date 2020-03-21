package com.wiblog.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员操作记录注解
 *
 * @author pwm
 * @date 2019/10/11
 */
@Retention(RetentionPolicy.RUNTIME)//运行时去动态获取注解信息
@Target(ElementType.METHOD)
public @interface OpsRecord {

    /**
     * 操作详情
     */
    String msg() default "";
}
