package com.wiblog.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.entity.UserRole;

/**
 *   服务类
 *
 * @author pwm
 * @since 2019-10-09
 */
public interface IUserRoleService extends IService<UserRole> {

    /**
     * 超级管理员分配权限
     * @param user 登录用户
     * @param uid uid
     * @param id 权限id
     * @return ServerResponse
     */
    ServerResponse assignPermission(User user, Long uid, Long id);

    /**
     * 获取某个用户权限
     * @param uid uid
     * @return ServerResponse
     */
    ServerResponse getUserRole(Long uid);

    /**
     * 获取权限类别
     * @return ServerResponse
     */
    ServerResponse getRole();

    /**
     * 校验权限级别
     * @param user user
     * @param grade grade
     * @return ServerResponse
     */
    ServerResponse checkAuthorize(User user,int grade);

    /**
     * 校验权限 是否是管理员
     * @param token token
     * @return Boolean
     */
    Boolean checkAuthorize(String token);
}
