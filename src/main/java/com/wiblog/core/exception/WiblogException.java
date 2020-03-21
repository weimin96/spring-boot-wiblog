package com.wiblog.core.exception;

/**
 * 自定义异常
 *
 * @author pwm
 * @date 2019/7/3
 */
public class WiblogException extends RuntimeException{

    public WiblogException() {
    }

    public WiblogException(String message) {
        super(message);
    }

    public WiblogException(String message, Throwable cause) {
        super(message, cause);
    }

    public WiblogException(Throwable cause) {
        super(cause);
    }
}
