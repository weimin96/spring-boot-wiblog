package com.wiblog.core.controller;

import com.wiblog.core.common.Constant;
import com.wiblog.core.utils.VerifyCodeUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/13
 */
@RestController
@Slf4j
public class CommonController {

    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        try {
            //设置相应类型,告诉浏览器输出的内容为图片
            response.setContentType("image/jpeg");
            //设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            VerifyCodeUtils.getRandomCode(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验验证码
     */
    @PostMapping("/checkVerify")
    public boolean checkVerify(String code, HttpSession session) {
        if (StringUtils.isNotBlank(code)) {
            String check = (String) session.getAttribute(Constant.VERIFY_CODE_SESSION_KEY);
            log.debug(check);
            return code.equalsIgnoreCase(check);
        }
        return false;
    }
}
