package com.wiblog.core.weixin;

/**
 * @author pwm
 * @date 2020/5/4
 */
public enum MsgEnum {
    /**
     * 文本消息
     */
    TEXT("text"),
    /**
     * 事件消息
     */
    EVENT("event");

    private String value;

    MsgEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MsgEnum fromString(String value) {
        for (MsgEnum type : MsgEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("不存在该类型" + value);
    }
}
