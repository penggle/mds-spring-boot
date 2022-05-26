package com.penglecode.codeforce.mybatismds.examples.user.domain.enums;

/**
 * 会员类型枚举
 *
 * @author pengpeng
 * @since 2.1
 */
public enum MemberTypeEnum {

    GENERAL("普通会员"),

    GOLDEN("黄金会员");

    private final String name;

    MemberTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
