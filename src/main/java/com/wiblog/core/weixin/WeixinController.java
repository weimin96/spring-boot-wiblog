package com.wiblog.core.weixin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/7/25
 */
@Controller
public class WeixinController {

    @Autowired
    private WeixinUtil weixinUtil;

    @GetMapping("/tokenCheck")
    @ResponseBody
    public void tokenCheck(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter print;
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            try {
                print = response.getWriter();
                print.write(echostr);
                print.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/weixin/getAccessToken")
    @ResponseBody
    public String getAccessToken() {
        return weixinUtil.getAccessToken();
    }

    @GetMapping("/weixin/sign")
    @ResponseBody
    public Map<String, String> sign(String url) {
        return weixinUtil.sign(url);
    }

    @GetMapping("/weixin/test")
    public String weixin() {
        return "test";
    }


}
