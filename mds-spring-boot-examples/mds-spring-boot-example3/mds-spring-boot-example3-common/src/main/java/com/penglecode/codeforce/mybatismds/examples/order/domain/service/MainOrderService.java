package com.penglecode.codeforce.mybatismds.examples.order.domain.service;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;

/**
 * 主订单领域服务接口
 *
 * @author pengpeng
 * @since 2.1
 */
public interface MainOrderService {

    /**
     * 创建主订单
     *
     * @param mainOrder
     * @return
     */
    void createMainOrder(MainOrder mainOrder);

    /**
     * 根据订单ID获取主订单信息
     *
     * @param orderId
     * @return
     */
    MainOrder getMainOrderById(Long orderId);

}
