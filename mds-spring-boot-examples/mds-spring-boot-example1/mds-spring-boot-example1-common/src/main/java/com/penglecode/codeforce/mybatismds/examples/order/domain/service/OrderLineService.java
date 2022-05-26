package com.penglecode.codeforce.mybatismds.examples.order.domain.service;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;

import java.util.List;

/**
 * 订单明细领域服务接口
 *
 * @author pengpeng
 * @since 2.1
 */
public interface OrderLineService {

    /**
     * 创建订单明细
     * @param orderLines
     */
    void createOrderLines(List<OrderLine> orderLines);

    /**
     * 根据订单ID获取订单明细
     *
     * @param orderId
     * @return
     */
    List<OrderLine> getOrderLinesByOrderId(Long orderId);

}
