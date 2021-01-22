package com.wiblog.core.common;

import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.utils.SpringContextUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础Controller
 *
 * @author pwm
 * @date 2019/7/3
 */
public abstract class BaseController {

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
