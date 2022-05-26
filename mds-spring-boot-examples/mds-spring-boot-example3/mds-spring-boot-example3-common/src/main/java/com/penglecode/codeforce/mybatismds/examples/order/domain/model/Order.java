package com.penglecode.codeforce.mybatismds.examples.order.domain.model;

import java.util.List;

/**
 * 订单聚合根
 *
 * @author pengpeng
 * @since 2.1
 */
public class Order extends MainOrder {

    /** 订单明细 */
    private List<OrderLine> orderLines;

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

}
