package com.wiblog.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 *   服务类
 *
 * @author pwm
 * @since 2019-06-01
 */
public interface IUserService extends IService<User> {
    /**
     * 登录
     * @param account 账号
     * @param password 密码
     * @return ServerResponse<User>
     */
    ServerResponse<User> login(String account, String password);

    /**
     * 注册
     * @param username username
     * @param phone phone
     * @param email email
     * @param password password
     */
    void register(String username,String phone,String email,String emailCode,String password,String[] address);

    /**
     * 校验用户名
     * @param username username
     */
    void checkUsername(String username);

    /**
     * 校验手机号
     * @param phone phone
     */
    void checkPhone(String phone);

    /**
     * 校验邮箱
     * @param email email
     */
    void checkEmail(String email);

    /**
     * 获取当前登录用户
     * @param request request
     * @return user
     */
    User loginUser(HttpServletRequest request);

    /**
     * 获取所有用户名
     * @return ServerResponse
     */
    ServerResponse getAllUsername();

    /**
     * 获取用户管理列表
     * @param state 用户状态
     * @param username 用户名模糊查询
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @param orderBy orderBy
     * @return ServerResponse
     */
    ServerResponse userManageListPage(Integer state, String username, Integer pageNum, Integer pageSize, String orderBy);

    /**
     * 获取用户所有信息
     * @param uid uid
     * @return ServerResponse
     */
    ServerResponse getBindingList(Long uid);

    /**
     * 绑定账号
     * @param uid uid
     * @param type type
     * @param val val
     * @param code code
     * @return ServerResponse
     */
    ServerResponse binding(Long uid, String type, String val, String code);

    /**
     * 用户注销
     * @param uid uid
     * @return ServerResponse
     */
    ServerResponse deleteUser(Long uid);

    /**
     * 解绑
     * @param uid uid
     * @param type type
     * @return ServerResponse
     */
    ServerResponse unBinding(Long uid, String type);

    /**
     * 设置用户信息
     * @param uid uid
     * @param userNew userNew
     * @return ServerResponse
     */
    ServerResponse setUserDetail(Long uid, User userNew);

    /**
     * 设置用户头像
     * @param uid uid
     * @param file file
     * @return ServerResponse
     */
    ServerResponse setAvatar(Long uid, MultipartFile file);
}
