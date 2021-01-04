package com.wiblog.core.weixin;

import lombok.Data;

/**
 * @author pwm
 * @date 2020/5/4
 */
@Data
public class TextMessage {

    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String Content;
    private String MsgId;
    private Integer FuncFlag;
}
