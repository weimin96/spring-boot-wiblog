package com.wiblog.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiblog.core.entity.User;
import com.wiblog.core.vo.UserVo;
import org.apache.ibatis.annotations.Options;

import java.util.List;
import java.util.Map;

/**
 * Mapper 接口
 *
 * @author pwm
 * @since 2019-06-01
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 插入用户表 返回id
     *
     * @param user user
     * @return id
     */
    @Options(useGeneratedKeys = true, keyProperty = "uid", keyColumn = "uid")
    int insertReturnId(User user);


    /**
     * 获取所有用户名
     *
     * @return List
     */
    List<Map<String, String>> selectUsername();

    /**
     * 获取所有用户信息
     *
     * @param page     page
     * @param state    state
     * @param username username
     * @return IPage
     */
    IPage<UserVo> selectUserManagePage(Page<UserVo> page, Integer state, String username);

    /**
     * 更新用户状态为0
     *
     * @param uid uid
     * @return int
     */
    int updateStateToZero(Long uid);

    /**
     * 修改用户信息
     *
     * @param user user
     * @return int
     */
    int updateDetail(User user);
}
