package com.wiblog.core.controller;


import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.BaseController;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户角色控制层
 *
 * @author pwm
 * @date 2019-10-09
 */
@RestController
@RequestMapping("/role")
@PropertySource(value = "classpath:/wiblog.properties", encoding = "utf-8")
public class UserRoleController extends BaseController {

    private final IUserRoleService userRoleService;

    private final IUserService userService;

    @Value("${admin-url}")
    private String adminUrl;

    @Autowired
    public UserRoleController(IUserRoleService userRoleService, IUserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }

    /**
     * 超级管理员分配权限
     *
     * @param request request
     * @param uid     用户id
     * @param roleId  角色id
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = RoleEnum.SUPER_ADMIN)
    @PostMapping("/assignPermission")
    public ServerResponse<?> assignPermission(HttpServletRequest request, Long uid, Long roleId) {
        User user = getLoginUser(request);
        return userRoleService.assignPermission(user, uid, roleId);
    }

    /**
     * 获取某个用户权限
     *
     * @param uid uid
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = RoleEnum.SUPER_ADMIN)
    @PostMapping("/getUserRole")
    public ServerResponse<?> getUserRole(Long uid) {
        return userRoleService.getUserRole(uid);
    }

    /**
     * 获取权限类别
     *
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = RoleEnum.SUPER_ADMIN)
    @PostMapping("/getAllRole")
    public ServerResponse<?> getAllRole() {
        return userRoleService.getRole();
    }

    /**
     * 管理员获取后台链接
     *
     * @param request request
     * @return ServerResponse
     */
    @GetMapping("/getAdminUrl")
    public ServerResponse<?> getAdminUrl(HttpServletRequest request) {
        User user = userService.loginUser(request);
        ServerResponse<?> response = userRoleService.checkAuthorize(user, RoleEnum.ADMIN);
        if (response.isSuccess()) {
            return ServerResponse.success(adminUrl);
        }
        return response;
    }
}
