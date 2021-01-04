package com.wiblog.core.interceptor;

import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 *
 * @author pwm
 * @date 2019/7/1
 */
@Component
@Slf4j
public class BaseInterceptor implements HandlerInterceptor {

    private final IUserService userService;

    private final IUserRoleService userRoleService;

    public BaseInterceptor(IUserService userService, IUserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();
        log.info("用户访问地址:{}, 来路地址: {}", uri, IPUtil.getIpAddr(request));

        //请求拦截处理
        User user = userService.loginUser(request);
        // 没有管理员权限不允许访问admin
        if (uri.startsWith("/admin") && !userRoleService.checkAuthorize(user, RoleEnum.ADMIN).isSuccess()) {
            response.sendRedirect(request.getContextPath() + "/");
            log.info("没有权限访问admin,来路地址: {}", IPUtil.getIpAddr(request));
            return false;
        }
        if (user != null) {
            request.setAttribute("user", user);
            return true;
        }
        return true;
    }
}
