package com.wiblog.core.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author pwm
 * @date 2019/4/13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private static final long serialVersionUID = -516174735253208470L;


    private final int code;
    private final String msg;
    private T data;
    private T extra;

    private ServerResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ServerResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int code, String msg, T data,T extra) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.extra = extra;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == ResultConstant.SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public T getExtra() {
        return extra;
    }

    public static <T> ServerResponse<T> success(T data, String msg) {
        return new ServerResponse<>(ResultConstant.SUCCESS_CODE, msg, data);
    }

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse<>(ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MSG, data);
    }

    public static <T> ServerResponse<T> success(T data, String msg,T extra) {
        return new ServerResponse<>(ResultConstant.SUCCESS_CODE, msg, data,extra);
    }

    public static <T> ServerResponse<T> error(String msg,int code) {
        return new ServerResponse<>(code, msg);
    }
}
