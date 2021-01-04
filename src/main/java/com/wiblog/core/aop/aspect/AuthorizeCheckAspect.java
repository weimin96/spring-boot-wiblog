package com.wiblog.core.aop.aspect;

import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.RoleEnum;
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
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 权限校验
 *
 * @author pwm
 * @date 2019/10/11
 */
@Component
@Aspect
public class AuthorizeCheckAspect {

    private final IUserRoleService userRoleService;
    private final IUserService userService;

    public AuthorizeCheckAspect(IUserRoleService userRoleService, IUserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    /**
     * 加入注解 @AuthorizeCheck 时触发
     */
    @Pointcut("@annotation(com.wiblog.core.aop.AuthorizeCheck)")
    public void controllerInterceptor() {

    }

    /**
     * controller层增强类
     *
     * @param pjp pjp
     * @return java.lang.Object
     * @throws Throwable Throwable
     */
    @Around("controllerInterceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();

        AuthorizeCheck authorizeCheck = method.getAnnotation(AuthorizeCheck.class);
        RoleEnum gradeEnum = authorizeCheck.grade();
        User user = userService.loginUser(request);
        // 检查当前登录用户的权限级别
        ServerResponse<?> serverResponse = userRoleService.checkAuthorize(user, gradeEnum);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        // 如果没有报错，放行
        return pjp.proceed();
    }
}
