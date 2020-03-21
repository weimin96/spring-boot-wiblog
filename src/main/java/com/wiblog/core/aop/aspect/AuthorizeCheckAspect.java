package com.wiblog.core.aop.aspect;

import com.alibaba.fastjson.JSON;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.service.IUserService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验
 *
 * @author pwm
 * @date 2019/10/11
 */
@Component
@Aspect
public class AuthorizeCheckAspect {

    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IUserService  userService;

    /**
     * 加入注解 @AuthorizeCheck 时触发
     */
    @Pointcut("@annotation(com.wiblog.core.aop.AuthorizeCheck)")
    public void controllerInterceptor() {

    }

    /**
     * controller层增强类
     * @param pjp pjp
     * @return java.lang.Object
     * @throws Throwable Throwable
     */
    @Around("controllerInterceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取注解的方法参数列表
        Object[] args = pjp.getArgs();

        // 获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();

        AuthorizeCheck authorizeCheck = method.getAnnotation(AuthorizeCheck.class);
        String gradeStr = authorizeCheck.grade();
        int grade;
        try{
            grade = Integer.parseInt(gradeStr);
        }catch (NumberFormatException e){
            throw new RuntimeException("使用@AuthorizeCheck注解时必须指定grade的值，且为数值类型");
        }
        User user = userService.loginUser(request);
        // 检查当前登录用户的权限级别
        ServerResponse serverResponse = userRoleService.checkAuthorize(user,grade);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        // 如果没有报错，放行
        return pjp.proceed();
    }
}
