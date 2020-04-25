package com.wiblog.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wiblog.core.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/4
 */
@Service
@Slf4j
public class MailServiceImpl {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 异步线程 发送邮件
     */
    @Async
    public void sendHtmlMail(String to,String title,String content){
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送异常",e);
            //e.printStackTrace();
        }
    }


    public boolean checkEmail(String email,String checkCode){
        if (StringUtils.isBlank(email) || StringUtils.isBlank(checkCode)){
            return false;
        }
        String code = (String) redisTemplate.opsForValue().get(Constant.CHECK_EMAIL_KEY+email);
        return code != null && code.equals(checkCode);
    }

    /**
     * 邮件监听发送
     */
    public void mailListener(){
        log.info("读取邮件消息队列");
        while (true){
            String messageJson = (String) redisTemplate.opsForList().leftPop(Constant.EMAIL_PUSH_KEY,0, TimeUnit.SECONDS);
            if (StringUtils.isNotBlank(messageJson)){
                Map message = JSONObject.parseObject(messageJson);
                sendHtmlMail((String)message.get("to"),(String)message.get("title"),(String)message.get("content"));
            }
        }

    }


}
