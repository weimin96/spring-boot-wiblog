package com.wiblog.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.entity.UserRole;
import com.wiblog.core.mapper.UserRoleMapper;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 *
 * @author pwm
 * @since 2019-10-09
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    private final UserRoleMapper userRoleMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public UserRoleServiceImpl(RedisTemplate<String, Object> redisTemplate, UserRoleMapper userRoleMapper) {
        this.redisTemplate = redisTemplate;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public ServerResponse<?> assignPermission(User user, Long uid, Long[] ids) {
        // 分配权限
        try {
            userRoleMapper.delete(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUid,uid));
            userRoleMapper.insertBatch(uid, ids);
            return ServerResponse.success(null, "权限分配成功");
        } catch (Exception e) {
            return ServerResponse.error("权限分配失败", 90003);
        }
    }

    @Override
    public ServerResponse<?> getUserRole(Long uid) {
        List<RoleVo> role = userRoleMapper.selectRoleByUid(uid);
        return ServerResponse.success(role);
    }

    @Override
    public ServerResponse<?> getRole() {
        List<RoleVo> list = userRoleMapper.selectRole();
        return ServerResponse.success(list);
    }

    @Override
    public ServerResponse<?> checkAuthorize(User user, RoleEnum grade) {
        if (user == null) {
            return ServerResponse.error("用户未登录", 40000);
        }
        List<RoleVo> roleVoList = userRoleMapper.selectRoleByUid(user.getUid());
        if (roleVoList != null) {
            for (RoleVo roleVo : roleVoList) {
                RoleEnum role = RoleEnum.toName(roleVo.getRoleId().intValue());
                if (grade.getValue() == role.getValue()) {
                    return ServerResponse.success(null, "权限校验成功");
                }
            }

        }
        return ServerResponse.error("没有权限", 40000);
    }

    @Override
    public Boolean checkAuthorize(String token) {
        if (StringUtils.isNotBlank(token)) {
            String userJson = (String) redisTemplate.opsForValue().get(Constant.RedisKey.LOGIN_REDIS_KEY + token);
            if (StringUtils.isNotBlank(userJson)) {
                User user = JSON.parseObject(userJson, User.class);
                ServerResponse<?> response = checkAuthorize(user, RoleEnum.ADMIN);
                return response.isSuccess();
            }
        }
        return false;
    }
}
