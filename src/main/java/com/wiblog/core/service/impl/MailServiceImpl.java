package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiblog.core.common.Constant;
import com.wiblog.core.entity.Article;
import com.wiblog.core.entity.Comment;
import com.wiblog.core.entity.UserAuth;
import com.wiblog.core.service.IArticleService;
import com.wiblog.core.service.ICommentService;
import com.wiblog.core.service.IMailService;
import com.wiblog.core.service.IUserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * TODO 描述
 *
 * @author pwm
 * @date 2019/11/4
 */
@Service
@Slf4j
public class MailServiceImpl implements IMailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IUserAuthService userAuthService;

    /**
     * 异步线程 发送邮件
     */
    @Override
    public void sendHtmlMail(String to, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送异常", e);
            //e.printStackTrace();
        }
    }


    @Override
    public boolean checkEmail(String email, String checkCode) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(checkCode)) {
            return false;
        }
        String code = (String) redisTemplate.opsForValue().get(Constant.RedisKey.CHECK_EMAIL_KEY + email);
        return code != null && code.equals(checkCode);
    }


    /**
     * 评论通知
     * 1.评论评论且被评论者与发表评论者不是同一个人时，被评论者收到通知
     * 2.评论文章时且评论者不是作者时收到通知
     *
     * @param comment comment
     */
    @Override
    public void commentNotice(Comment comment) {
        Long toUid = null;
        // xx在<<xxx>>（url）中回复了你的评论，
        // xx在<<xxx>>（url）中评论了你的文章
        String content = "";
        LambdaQueryWrapper<Article> query = new QueryWrapper<Article>().lambda();
        query.eq(Article::getId, comment.getArticleId());
        Article article = articleService.getOne(query);
        // 回复评论
        if (comment.getParentId() != 0) {
            // 查找被评论人
            LambdaQueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>().lambda();
            queryWrapper.eq(Comment::getId, comment.getParentId());
            Comment toComment = commentService.getOne(queryWrapper);
            // 不是同一个人
            if (toComment != null && !comment.getUid().equals(toComment.getUid())) {
                toUid = toComment.getUid();
                content = "回复了你";
            }
        } else {
            if (article != null && !article.getUid().equals(comment.getUid())) {
                toUid = article.getUid();
                content = "评论了你的文章";
            }
        }
        if (toUid == null) {
            return;
        }
        // 被评论的人
        LambdaQueryWrapper<UserAuth> queryWrapper = new QueryWrapper<UserAuth>().lambda();
        queryWrapper.eq(UserAuth::getUid, toUid).eq(UserAuth::getIdentityType, "email");
        UserAuth userAuth = userAuthService.getOne(queryWrapper);
        // 有注册邮箱
        if (userAuth != null) {
            // 评论的人
            LambdaQueryWrapper<UserAuth> wrapper = new QueryWrapper<UserAuth>().lambda();
            wrapper.eq(UserAuth::getUid, comment.getUid()).eq(UserAuth::getIdentityType, "username");
            UserAuth fromUser = userAuthService.getOne(wrapper);
            StringBuilder sb = new StringBuilder();
            try (Reader reader = new InputStreamReader(new FileInputStream(ResourceUtils.getFile("classpath:replyMail.vm")))) {
                int s;
                while ((s = reader.read()) != -1) {
                    sb.append((char) s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message = sb.toString();
            // 评论人名称
            message = message.replace("[0]", fromUser.getIdentifier());
            // 文章链接
            message = message.replace("[1]", article.getArticleUrl());
            // 文章标题
            message = message.replace("[2]", article.getTitle());
            // 额外字段
            message = message.replace("[3]", content);
            // 评论内容
            message = message.replace("[4]", comment.getContent());
            sendHtmlMail(userAuth.getIdentifier(), "你有新的通知", message);
        }
    }
}
