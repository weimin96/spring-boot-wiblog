package com.wiblog.core.weixin;

/**
 * @author pwm
 * @date 2020/5/4
 */
public enum EventEnum {
    /**
     * 订阅类型
     */
    SUBSCRIBE("subscribe");

    private String value;

    EventEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventEnum fromString(String value) {
        for (EventEnum type : EventEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("不存在该类型" + value);
    }
}
