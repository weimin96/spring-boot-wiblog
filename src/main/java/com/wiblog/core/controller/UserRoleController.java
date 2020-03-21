package com.wiblog.core.controller;


import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.aop.OpsRecord;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.service.IUserRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 *  控制层
 *
 * @author pwm
 * @date 2019-10-09
 */
@RestController
@RequestMapping("/role")
public class UserRoleController extends BaseController{

    private IUserRoleService userRoleService;

    @Autowired
    public UserRoleController(IUserRoleService userRoleService){
        this.userRoleService = userRoleService;
    }

    /**
     * 超级管理员分配权限
     * @param request request
     * @param uid 用户id
     * @param roleId 角色id
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "1")
    @PostMapping("/assignPermission")
    public ServerResponse assignPermission(HttpServletRequest request,Long uid,Long roleId){
        // 是否超级管理员
        User user = getLoginUser(request);
        return userRoleService.assignPermission(user,uid,roleId);
    }

    /**
     * 获取某个用户权限
     * @param request request
     * @param uid uid
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "1")
    @PostMapping("/getUserRole")
    public ServerResponse getUserRole(HttpServletRequest request,Long uid){
        return userRoleService.getUserRole(uid);
    }

    /**
     * 获取权限类别
     * @param request request
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = "1")
    @PostMapping("/getAllRole")
    public ServerResponse getAllRole(HttpServletRequest request){
        return userRoleService.getRole();
    }
}
