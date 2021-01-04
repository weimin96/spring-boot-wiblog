package com.wiblog.core.controller;


import com.alibaba.fastjson.JSON;
import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.BaseController;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.User;
import com.wiblog.core.exception.WiblogException;
import com.wiblog.core.service.IUserRoleService;
import com.wiblog.core.service.IUserService;
import com.wiblog.core.thirdparty.GithubProvider;
import com.wiblog.core.utils.IPUtil;
import com.wiblog.core.utils.Md5Util;
import com.wiblog.core.utils.WiblogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 控制层
 *
 * @author pwm
 * @date 2019-06-01
 */
@RestController
@RequestMapping("/u")
@Slf4j
public class UserController extends BaseController {

    private final RedisTemplate<String, Object> redisTemplate;

    private final IUserService userService;

    private final IUserRoleService userRoleService;

    private final GithubProvider githubProvider;

    @Autowired
    public UserController(IUserService userService, GithubProvider githubProvider, RedisTemplate<String, Object> redisTemplate, IUserRoleService userRoleService) {
        this.userService = userService;
        this.githubProvider = githubProvider;
        this.redisTemplate = redisTemplate;
        this.userRoleService = userRoleService;
    }

    @PostMapping("/login")
    public ServerResponse<?> login(String account, String password, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("登录");
        // 错误次数
        Integer errorCount = cache.get("login_error_count");
        ServerResponse<User> serverResponse;
        try {
            serverResponse = userService.login(account, password);
            User user = serverResponse.getData();
            // redis缓存
            String token = Md5Util.MD5(request.getSession().getId() + user.getUid().toString());
            redisTemplate.opsForValue().set(Constant.RedisKey.LOGIN_REDIS_KEY + token, JSON.toJSONString(user), 7, TimeUnit.DAYS);
            // cookies
            WiblogUtil.setCookie(response, token);
            // TODO 登录日志

        } catch (Exception e) {
            if (errorCount == null) {
                errorCount = 1;
            } else {
                errorCount++;
            }
            if (errorCount > 3) {
                return ServerResponse.error("您输入密码已经错误超过3次，请10分钟后尝试", 10005);
            }
            cache.set("login_error_count", errorCount, 10 * 60);
            String msg = "登录失败";
            if (e instanceof WiblogException) {
                msg = e.getMessage();
            } else {
                log.warn(msg, e);
            }
            return ServerResponse.error(msg, 10004);
        }
        return serverResponse;
    }

    @GetMapping("/logout")
    public ServerResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = WiblogUtil.getCookie(request, Constant.COOKIES_KEY);
        if (StringUtils.isNotBlank(token)) {
            WiblogUtil.delCookie(request, response);
            Boolean bool = redisTemplate.delete(Constant.RedisKey.LOGIN_REDIS_KEY + token);
            log.info("退出登录{}", bool);
        }
        return ServerResponse.success(null, "退出成功");
    }

    @PostMapping("/register")
    public ServerResponse<?> register(HttpServletRequest request, String username, String phone, String email, String password, String emailCode) {
        try {
            String ip = IPUtil.getIpAddr(request);
            String[] address = IPUtil.getIpInfo(ip);
            log.info("用户地址{}-{}", address[0], address[1]);
            userService.register(username, phone, email, emailCode, password, address);
        } catch (Exception e) {
            String msg = "注册失败";
            if (e instanceof WiblogException) {
                msg = e.getMessage();
            } else {
                log.warn(msg, e);
            }
            return ServerResponse.error(msg, 30001);
        }
        return ServerResponse.success(null);
    }

    /**
     * 获取用户基础信息
     *
     * @param id id
     * @return ServerResponse
     */
    @GetMapping("/info")
    public ServerResponse<?> getUserById(Long id) {
        User user = userService.getById(id);
        return ServerResponse.success(user);
    }


    @PostMapping("/checkUsername")
    public ServerResponse<?> checkUsername(String value) {
        try {
            userService.checkUsername(value);
        } catch (WiblogException e) {
            return ServerResponse.error(e.getMessage(), 30001);
        }
        return ServerResponse.success("用户名校验成功");
    }

    @PostMapping("/checkPhone")
    public ServerResponse<?> checkPhone(String value) {
        try {
            userService.checkPhone(value);
        } catch (WiblogException e) {
            return ServerResponse.error(e.getMessage(), 30001);
        }
        return ServerResponse.success("手机号校验成功");
    }

    @PostMapping("/checkEmail")
    public ServerResponse<?> checkEmail(String value) {
        try {
            userService.checkEmail(value);
        } catch (WiblogException e) {
            return ServerResponse.error(e.getMessage(), 30001);
        }
        return ServerResponse.success("手机号校验成功");
    }

    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @GetMapping("/getAllUsername")
    public ServerResponse<?> getAllUsername() {
        return userService.getAllUsername();
    }

    /**
     * 获取用户管理列表
     *
     * @param state    用户状态
     * @param username 用户名模糊查询
     * @param pageNum  pageNum
     * @param pageSize pageSize
     * @param orderBy  orderBy
     * @return ServerResponse
     */
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    @PostMapping("/userManageListPage")
    public ServerResponse<?> userManageListPage(
            @RequestParam(value = "state", required = false) Integer state,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy) {
        return userService.userManageListPage(state, username, pageNum, pageSize, orderBy);
    }

    @PostMapping("/deleteUser")
    public ServerResponse<?> deleteUser(HttpServletRequest request, Long id) {
        // 删除自己
        if (id == null) {
            User user = getLoginUser(request);
            if (user == null) {
                return ServerResponse.error("用户未登录", 30001);
            }
            return userService.deleteUser(user.getUid());
        } else {
            // 管理员权限
            String token = WiblogUtil.getCookie(request, Constant.COOKIES_KEY);
            boolean isManager = userRoleService.checkAuthorize(token);
            if (isManager) {
                return userService.deleteUser(id);
            } else {
                return ServerResponse.error("没有权限", 30001);
            }
        }
    }

    /**
     * github 登录回调
     *
     * @param request  request
     * @param response response
     * @param code     code
     */
    @GetMapping("/github/callback")
    public ServerResponse<?> githubLogin(HttpServletRequest request, HttpServletResponse response, String code, String state) throws IOException {
        String accessToken = githubProvider.getAccessToken(code, state);
        Map githubUser = githubProvider.getUser(accessToken);
        if ("login".equals(state)) {
            User user = githubProvider.registerGithub(githubUser, accessToken, request);
            // redis缓存
            String token = Md5Util.MD5(request.getSession().getId() + user.getUid().toString());
            redisTemplate.opsForValue().set(Constant.RedisKey.LOGIN_REDIS_KEY + token, JSON.toJSONString(user), 7, TimeUnit.DAYS);
            // cookies
            WiblogUtil.setCookie(response, token);
            // 跳转历史页面
            String url = WiblogUtil.getCookie(request, "back");
            log.info(url);
            response.sendRedirect(url);
            return null;
        } else {
            User user = getLoginUser(request);
            if (user != null) {
                ServerResponse<?> serverResponse = githubProvider.bingGithub(user.getUid(), githubUser, accessToken);
                String url = WiblogUtil.getCookie(request, "back");
                if (!serverResponse.isSuccess()) {
                    WiblogUtil.setCookie(response, "error", serverResponse.getMsg(), 60);
                }
                response.sendRedirect(url);
            }
            return ServerResponse.error("用户未登录", 30001);
        }
    }


    @GetMapping("/getBindingList")
    public ServerResponse<?> getBindingList(HttpServletRequest request) {
        User user = getLoginUser(request);
        if (user != null) {
            return userService.getBindingList(user.getUid());
        }
        return ServerResponse.error("用户未登录", 30001);
    }

    /**
     * 绑定手机号或邮箱
     *
     * @param request request
     * @param type    type
     * @param val     val
     * @param code    code
     * @return ServerResponse
     */
    @PostMapping("/binding")
    public ServerResponse<?> binding(HttpServletRequest request, String type, String val, String code) {
        User user = getLoginUser(request);
        if (user != null) {
            return userService.binding(user.getUid(), type, val, code);
        }
        return ServerResponse.error("用户未登录", 30001);
    }

    /**
     * 绑解手机号或邮箱
     *
     * @param request request
     * @param type    type
     * @return ServerResponse
     */
    @PostMapping("/unBinding")
    public ServerResponse<?> unBinding(HttpServletRequest request, String type) {
        User user = getLoginUser(request);
        if (user != null) {
            return userService.unBinding(user.getUid(), type);
        }
        return ServerResponse.error("用户未登录", 30001);
    }

    @PostMapping("/setUserDetail")
    public ServerResponse<?> setUserDetail(HttpServletRequest request, User userNew) {
        User user = getLoginUser(request);
        if (user != null) {
            ServerResponse<?> response = userService.setUserDetail(user.getUid(), userNew);
            if (response.isSuccess()) {
                user.setCity(userNew.getCity());
                user.setSex(userNew.getSex());
                user.setIntro(userNew.getIntro());
                String token = WiblogUtil.getCookie(request, Constant.COOKIES_KEY);
                redisTemplate.opsForValue().set(Constant.RedisKey.LOGIN_REDIS_KEY + token, JSON.toJSONString(user), 7, TimeUnit.DAYS);
            }
            return response;
        }
        return ServerResponse.error("用户未登录", 30001);
    }

    @PostMapping("/setAvatar")
    public ServerResponse<?> setAvatar(HttpServletRequest request, MultipartFile file) {
        User user = getLoginUser(request);
        if (user != null) {
            ServerResponse<?> response = userService.setAvatar(user.getUid(), file);
            if (response.isSuccess()) {
                user.setAvatarImg((String) response.getData());
                String token = WiblogUtil.getCookie(request, Constant.COOKIES_KEY);
                log.info("token={}", token);
                redisTemplate.opsForValue().set(Constant.RedisKey.LOGIN_REDIS_KEY + token, JSON.toJSONString(user), 7, TimeUnit.DAYS);
            }
            return response;
        }
        return ServerResponse.error("用户未登录", 30001);
    }
}
