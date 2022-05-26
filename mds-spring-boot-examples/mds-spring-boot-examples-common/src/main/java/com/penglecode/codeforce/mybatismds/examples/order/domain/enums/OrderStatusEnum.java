package com.penglecode.codeforce.mybatismds.examples.order.domain.enums;

/**
 * 订单状态
 *
 * @author pengpeng
 * @since 2.1
 */
public enum OrderStatusEnum {

    WPAY("待付款"),

    PAID("待发货"),

    REFUND("退款中"),

    CLOSED("已完成");

    private final String name;

    OrderStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
