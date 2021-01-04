package com.wiblog.core.listener;

import com.wiblog.core.event.CommentEvent;
import com.wiblog.core.event.MailEvent;
import com.wiblog.core.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 邮件监听器
 * @author pwm
 * @date 2021/1/4
 */
@Slf4j
@Component
public class MailListener implements ApplicationListener<MailEvent> {

    @Autowired
    private IMailService mailService;

    @Async
    @Override
    public void onApplicationEvent(MailEvent mailEvent) {
        log.info("接收到邮件事件");
        mailService.sendHtmlMail(mailEvent.getEmail(), mailEvent.getTitle(), mailEvent.getMessage());
    }
}
