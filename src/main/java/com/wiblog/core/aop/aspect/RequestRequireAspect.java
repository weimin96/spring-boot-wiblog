package com.wiblog.core.aop.aspect;

import com.wiblog.core.aop.RequestRequire;
import com.wiblog.core.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * aop拦截 参数校验
 *
 * @author pwm
 * @date 2019/6/3
 */
@Component
@Aspect
@Slf4j
public class RequestRequireAspect {

    private static final String SPLIT = ",";

    /**
     * 加入注解 @RequestRequire 时触发
     */
    @Pointcut("@annotation(com.wiblog.core.aop.RequestRequire)")
    public void controllerInterceptor() {

    }

    /**
     * controller层增强类，用于检测参数为空的情况
     * @param pjp pjp
     * @return java.lang.Object
     * @throws Throwable Throwable
     */
    @Around("controllerInterceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        // 获取注解的方法参数列表
        Object[] args = pjp.getArgs();

        // 获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        RequestRequire require = method.getAnnotation(RequestRequire.class);

        // 以防万一，将中文的逗号替换成英文的逗号
        String fieldNames = require.require().replace("，", ",");

        // 从参数列表中获取参数对象
        Object parameter = null;
        for (Object pa : args) {
            //class相等表示是同一个对象
            if (pa.getClass() == require.parameter()) {
                parameter = pa;
            }
        }

        // 通过反射去和指定的属性值判断是否非空
        // 获得参数的class
        Class aClass;
        if (parameter != null) {
            aClass = parameter.getClass();
        } else {
            throw new RuntimeException("使用@RequestRequire注解时必须指定parameter的值");
        }


        // 遍历参数，找到是否为空
        for (String name : fieldNames.split(SPLIT)) {
            Field declaredField = aClass.getDeclaredField(name);
            String fieldName = declaredField.getName();
            declaredField.setAccessible(true);
            Object fieldObject = declaredField.get(parameter);
            // declaredField.getGenericType().toString()
            if (fieldObject == null || "".equals(fieldObject.toString())) {
                return ServerResponse.error("参数" + fieldName + "不能为空", 10004);
            }
        }
        // 如果没有报错，放行
        return pjp.proceed();
    }
}
