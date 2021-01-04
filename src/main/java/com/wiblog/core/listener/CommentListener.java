package com.wiblog.core.listener;

import com.wiblog.core.event.CommentEvent;
import com.wiblog.core.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author pwm
 * @date 2021/1/4
 */
@Slf4j
@Component
public class CommentListener implements ApplicationListener<CommentEvent> {

    @Autowired
    private IMailService mailService;

    @Async
    @Override
    public void onApplicationEvent(CommentEvent commentEvent) {
        log.info("接收到评论");
        mailService.commentNotice(commentEvent.getComment());
    }
}
