package com.wiblog.core.listener;

import com.wiblog.core.event.CommentEvent;
import com.wiblog.core.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/**
 * @author pwm
 * @date 2021/1/4
 */
@Slf4j
@Component
public class CommentListener implements ApplicationListener<CommentEvent> {

    @Autowired
    private IMailService mailService;

    @Resource(name = "taskExecutor")
    private ExecutorService executorService;

    @Override
    public void onApplicationEvent(CommentEvent commentEvent) {
        log.info("接收到评论");
        executorService.execute(()->{
            mailService.commentNotice(commentEvent.getComment());
        });
    }
}
