package com.wiblog.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.entity.UserRole;
import com.wiblog.core.mapper.UserRoleMapper;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author pwm
 * @since 2019-10-09
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ServerResponse assignPermission(User user, Long uid, Long id) {
        // 分配权限
        UserRole userRole = new UserRole();
        userRole.setUid(uid);
        userRole.setRoleId(id);
        int count = userRoleMapper.insertOrUpdate(userRole);
        if (count <=0){
            return ServerResponse.error("权限分配失败",90003);
        }
        return ServerResponse.success(null,"权限分配成功");
    }

    @Override
    public ServerResponse getUserRole(Long uid) {
        RoleVo role = userRoleMapper.selectRoleByUid(uid);
        return ServerResponse.success(role);
    }

    @Override
    public ServerResponse getRole() {
        List<RoleVo> list = userRoleMapper.selectRole();
        return ServerResponse.success(list);
    }

    @Override
    public ServerResponse checkAuthorize(User user,int grade) {
        if (user == null){
            return ServerResponse.error("用户未登录",40000);
        }
        RoleVo roleVo = userRoleMapper.selectRoleByUid(user.getUid());
        // 没有权限
        if(roleVo == null || roleVo.getRoleId()>grade){
            return ServerResponse.error("没有权限",40000);
        }
        return ServerResponse.success(null,"权限校验成功");
    }

    @Override
    public Boolean checkAuthorize(String token) {
        if (StringUtils.isNotBlank(token)) {
            String userJson = (String) redisTemplate.opsForValue().get(Constant.LOGIN_REDIS_KEY + token);
            if (StringUtils.isNotBlank(userJson)) {
                User user = JSON.parseObject(userJson, User.class);
                ServerResponse response = checkAuthorize(user,2);
                return response.isSuccess();
            }
        }
        return false;
    }
}
