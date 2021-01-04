package com.wiblog.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiblog.core.entity.UserRole;
import com.wiblog.core.vo.RoleVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author pwm
 * @since 2019-10-09
 */
@Component
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 查找用户角色
     *
     * @param uid uid
     * @return List
     */
    RoleVo selectRoleByUid(Long uid);

    /**
     * 查找权限类别
     *
     * @return List
     */
    List<RoleVo> selectRole();
}
