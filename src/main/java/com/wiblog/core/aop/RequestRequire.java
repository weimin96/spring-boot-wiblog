package com.wiblog.core.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 非空判断注解
 *
 * @author pwm
 * @date 2019/6/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestRequire {

    /**
     * 请求当前接口所需要的参数,多个以小写的逗号隔开
     */
    String require() default "";

    /**
     *传递参数的对象类型
     */
    Class<?> parameter() default Object.class;
}
