package com.wiblog.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author pwm
 * @date 2021/1/4
 */
public class MailEvent extends ApplicationEvent {

    private String email;

    private String title;

    private String message;

    public MailEvent(Object source,String email,String title,String message) {
        super(source);
        this.title = title;
        this.message = message;
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
