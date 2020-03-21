package com.wiblog.core.aop.aspect;

import com.wiblog.core.aop.OpsRecord;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Ops;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IOpsService;
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
import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员操作记录
 *
 * @author pwm
 * @date 2019/10/11
 */
@Component
@Aspect
public class OpsRecordAspect {


    @Autowired
    private IUserService userService;

    @Autowired
    private IOpsService opsService;

    /**
     * 加入注解 @OpsRecord 时触发
     */
    @Pointcut("@annotation(com.wiblog.core.aop.OpsRecord)")
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
        HttpServletRequest request = attributes.getRequest();

        // 获取注解的方法参数列表
        Object[] args = pjp.getArgs();

        // 获取被注解的方法
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Method method = signature.getMethod();

        ServerResponse result = (ServerResponse) pjp.proceed();
        if (result.isSuccess()){
            OpsRecord opsRecord = method.getAnnotation(OpsRecord.class);
            String msg = opsRecord.msg();
            msg = MessageFormat.format(msg,result.getExtra());
            User user = userService.loginUser(request);
            // 插入记录
            Ops ops = new Ops();
            ops.setUsername(user.getUsername());
            ops.setMsg(msg);
            Date date = new Date();
            ops.setCreateTime(date);
            ops.setUpdateTime(date);
            opsService.save(ops);
        }
        // 如果没有报错，放行
        return result;
    }
}
