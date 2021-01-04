package com.wiblog.core.event;

import com.wiblog.core.entity.Comment;
import org.springframework.context.ApplicationEvent;

/**
 * 评论事件
 * @author pwm
 * @date 2021/1/4
 */
public class CommentEvent extends ApplicationEvent {

    private Comment comment;

    public CommentEvent(Object source,Comment comment) {
        super(source);
        this.comment = comment;
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
