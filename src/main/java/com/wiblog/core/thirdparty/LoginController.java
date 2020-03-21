package com.wiblog.core.thirdparty;

import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/1
 */
@RestController
public class LoginController {



    @Autowired
    private IUserService userService;


}
