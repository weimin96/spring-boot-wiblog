package com.wiblog.core.service;

import com.wiblog.core.entity.Comment;
import org.springframework.stereotype.Service;

/**
 * @author pwm
 * @date 2021/1/3
 */
@Service
public interface IMailService {

    void sendHtmlMail(String to, String title, String content);

    void commentNotice(Comment comment);

    boolean checkEmail(String email, String checkCode);
}
