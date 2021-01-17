package com.wiblog.core.common;

/**
 * @author pwm
 * @date 2019/4/16
 */
public class ResultConstant {

    public static final String SUCCESS_MSG = "请求成功";
    public static final String PARA_SUCCESS_MSG = "参数校验功";
    public static final int SUCCESS_CODE = 10000;

    public static final String PARA_ILLEGAL_MSG = "参数不合法";
    public static final int PARA_ILLEGAL_CODE = 20001;

    public interface UserCenter{
        String LOGIN_SUCCESS = "登录成功";
        String REGISTER_SUCCESS = "登录成功";

        String NOT_ALLOW_NULL_MSG = "用户名和密码不能为空";
        int NOT_ALLOW_NULL_CODE = 30001;

        String MATCH_ERROR_MSG = "用户名或密码错误";
        int MATCH_ERROR_CODE = 30002;

        String USERNAME_ERROR_MSG = "用户名不能为纯数字或带有特殊字符";
        int USERNAME_ERROR_CODE = 30011;

        String USERNAME_LEN_ERROR_MSG = "用户名长度必须大于4个字符且小于32字符";
        int USERNAME_LEN_ERROR_CODE = 30012;

        String USERNAME_EXIT_MSG = "用户名已存在";
        int USERNAME_EXIT_CODE = 300013;

        String PHONE_ERROR_MSG = "手机号格式不正确";
        int PHONE_ERROR_CODE = 30021;

        String PHONE_EXIT_MSG = "手机号已存在";
        int PHONE_EXIT_CODE = 30022;

        String EMAIL_ERROR_MSG = "邮箱格式不正确";
        int EMAIL_ERROR_CODE = 30031;

        String EMAIL_EXIT_MSG = "邮箱已存在";
        int EMAIL_EXIT_CODE = 30032;

        String REGISTER_ERROR_MSG = "注册失败";
        int REGISTER_ERROR_CODE = 30009;
    }
}
