package com.wiblog.core.common;

import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.utils.MapCache;
import com.wiblog.core.utils.SpringContextUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础Controller
 *
 * @author pwm
 * @date 2019/7/3
 */
public abstract class BaseController {

    protected MapCache cache = MapCache.single();

    /**
     * 获取登录用户信息
     *
     * @param request request
     * @return User
     */
    public User getLoginUser(HttpServletRequest request) {
        IUserService userService = (IUserService)SpringContextUtil.getBean(IUserService.class);
        return userService.loginUser(request);
    }

}
