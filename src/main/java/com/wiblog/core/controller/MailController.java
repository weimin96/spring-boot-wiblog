package com.wiblog.core.controller;

import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.event.MailEvent;
import com.wiblog.core.service.IMailService;
import com.wiblog.core.service.impl.MailServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/4
 */
@RestController
public class MailController {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private IMailService mailService;

    private final RedisTemplate<String, Object> redisTemplate;

    public MailController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/getEmailCheckCode")
    public ServerResponse<?> getEmailCheckCode(String email) {
        // 5分钟内只允许发3条
        String countStr = (String) redisTemplate.opsForValue().get(Constant.RedisKey.EMAIL_COUNT+email);
        int count = StringUtils.isBlank(countStr)?1:Integer.parseInt(countStr);
        if (count == 2){
            count++;
        }else if (count>=3){
            return ServerResponse.error("请等5分钟后重试",30001);
        }
        redisTemplate.opsForValue().set(Constant.RedisKey.EMAIL_COUNT+email,count+"",5,TimeUnit.MINUTES);

        int checkCode = new Random().nextInt(9000) + 1000;
        // 有效期1天
        redisTemplate.opsForValue().set(Constant.RedisKey.CHECK_EMAIL_KEY + email, checkCode+"", 1, TimeUnit.DAYS);
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(new FileInputStream(ResourceUtils.getFile("classpath:mail.vm")))){
            int s = 0;
            while ((s = reader.read())!= -1){

                sb.append((char) s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = sb.toString();
        message=message.replace("[0]",email);
        message=message.replace("[1]",checkCode+"");
        // 发布邮件事件
        publisher.publishEvent(new MailEvent(this,email,"请激活你的邮箱账号", message));
        return ServerResponse.success(null);
    }

    @PostMapping("/checkEmailCode")
    public ServerResponse<?> checkEmailCode(String email,String code) {
        boolean bool = mailService.checkEmail(email,code);
        if (bool) {
            return ServerResponse.success("邮箱验证成功");
        }
        return ServerResponse.error("验证码错误",30001);
    }


}
