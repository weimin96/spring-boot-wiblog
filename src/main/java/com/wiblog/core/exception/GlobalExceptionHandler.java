package com.wiblog.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/10
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e){
        log.error("find exception:",e);
        return "/error/500.html";
    }

}
