package com.penglecode.codeforce.mybatismds.examples.order.app.service;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.Order;

/**
 * 订单应用服务接口
 *
 * @author pengpeng
 * @since 2.1
 */
public interface OrderAppService {

    /**
     * 创建订单
     * @param order
     */
    void createOrder1(Order order);

    /**
     * 创建订单
     * @param order
     */
    void createOrder2(Order order);

    /**
     * 根据ID获取订单
     * @param orderId
     * @return
     */
    Order getOrderById(Long orderId);

}
