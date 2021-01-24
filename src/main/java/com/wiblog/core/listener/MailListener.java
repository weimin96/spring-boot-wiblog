package com.wiblog.core.listener;

import com.wiblog.core.event.MailEvent;
import com.wiblog.core.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

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

    @Resource(name = "taskExecutor")
    private ExecutorService executorService;

    @Override
    public void onApplicationEvent(MailEvent mailEvent) {
        log.info("接收到邮件事件");
        executorService.execute(()->{
            mailService.sendHtmlMail(mailEvent.getEmail(), mailEvent.getTitle(), mailEvent.getMessage());
        });

    }
}
