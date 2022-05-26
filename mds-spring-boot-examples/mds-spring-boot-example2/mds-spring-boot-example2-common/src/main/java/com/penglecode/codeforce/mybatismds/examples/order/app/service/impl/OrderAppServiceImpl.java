package com.penglecode.codeforce.mybatismds.examples.order.app.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.mybatismds.examples.order.app.service.OrderAppService;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.Order;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.MainOrderService;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.OrderLineService;
import com.penglecode.codeforce.mybatismds.examples.product.domain.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单应用服务实现
 *
 * @author pengpeng
 * @since 2.1
 */
@Service
public class OrderAppServiceImpl implements OrderAppService {

    @Resource
    private MainOrderService mainOrderService;

    @Resource
    private OrderLineService orderLineService;

    @Resource
    private ProductService productService;

    @Override
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void createOrder1(Order order) {
        initOrder(order);
        mainOrderService.createMainOrder(order); //创建主订单
        orderLineService.createOrderLines(order.getOrderLines()); //创建订单明细
        for(OrderLine orderLine : order.getOrderLines()) {
            //decrProductInventory1()方法上设置的事务传播特性是Propagation.REQUIRED
            productService.decrProductInventory1(orderLine.getProductId(), orderLine.getQuantity()); //扣库存
        }
    }

    @Override
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void createOrder2(Order order) {
        initOrder(order);
        mainOrderService.createMainOrder(order); //创建主订单
        orderLineService.createOrderLines(order.getOrderLines()); //创建订单明细
        for(OrderLine orderLine : order.getOrderLines()) {
            //decrProductInventory2()方法上设置的事务传播特性是Propagation.REQUIRES_NEW
            productService.decrProductInventory2(orderLine.getProductId(), orderLine.getQuantity()); //扣库存
        }
        Assert.isTrue(order.getOrderId().equals(System.currentTimeMillis()), "我不是故意的：测试Propagation.REQUIRES_NEW");
    }

    @Override
    public Order getOrderById(Long orderId) {
        MainOrder mainOrder = mainOrderService.getMainOrderById(orderId);
        List<OrderLine> orderLines = orderLineService.getOrderLinesByOrderId(orderId);
        Order order = new Order();
        BeanUtils.copyProperties(mainOrder, order);
        order.setOrderLines(orderLines);
        return order;
    }

    protected void initOrder(Order order) {
        String nowTime = DateTimeUtils.formatNow();
        order.setOrderTime(nowTime);
        order.setCreateTime(nowTime);
        order.setUpdateTime(nowTime);
        for(OrderLine orderLine : order.getOrderLines()) {
            orderLine.setOrderId(order.getOrderId());
            orderLine.setOrderTime(nowTime);
            orderLine.setCreateTime(nowTime);
            orderLine.setUpdateTime(nowTime);
            orderLine.setSubTotalAmount(orderLine.getUnitPrice() * orderLine.getQuantity() + orderLine.getFreight());
            order.setTotalFreight(order.getTotalFreight() + orderLine.getFreight());
            order.setTotalAmount(order.getTotalAmount() + orderLine.getSubTotalAmount());
        }
    }

}
