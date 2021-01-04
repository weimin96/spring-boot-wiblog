package com.wiblog.core.common;

/**
 * @author pwm
 * @date 2020/12/29
 */
public enum RoleEnum {

    /**
     * 对应数据库role
     */
    SUPER_ADMIN(1, "超级管理员"),
    ADMIN(2, "管理员"),
    USER(3, "超级管理员");

    private final String name;

    private final int value;

    RoleEnum(int value, String name) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static RoleEnum toName(int value) {
        for (RoleEnum type : RoleEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }

        throw new IllegalArgumentException("不存在该类型" + value);
    }
}
