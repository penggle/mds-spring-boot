package com.penglecode.codeforce.mybatismds.examples.test;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.mybatismds.examples.CsmdsExample2Application;
import com.penglecode.codeforce.mybatismds.examples.order.app.service.OrderAppService;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.Order;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;
import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import com.penglecode.codeforce.mybatismds.examples.product.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品模块的Mapper测试
 *
 * @author pengpeng
 * @since 2.1
 */
@SpringBootTest(classes=CsmdsExample2Application.class)
public class OrderServiceTest {

    @Autowired
    private OrderAppService orderAppService;

    @Autowired
    private ProductService productService;

    /**
     * 测试Propagation.REQUIRED事务传播特性
     * (连续执行两次，第一次成功，第二次因为减库存导致库存为负数报异常导致全部回滚)
     */
    @Test
    public void createOrder1() {
        String nowTime = DateTimeUtils.formatNow();
        Order order = new Order();
        order.setOrderId(System.currentTimeMillis());
        order.setOrderTime(nowTime);
        order.setOrderStatus(OrderStatusEnum.WPAY);
        order.setTotalAmount(0L);
        order.setTotalFreight(0L);
        order.setUserId(1L);
        order.setCreateTime(nowTime);

        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(createOrderLine(10047932928981L, 2));
        orderLines.add(createOrderLine(100028056874L, 2));
        order.setOrderLines(orderLines);
        orderAppService.createOrder1(order);
    }

    /**
     * 测试Propagation.REQUIRES_NEW事务传播特性
     * (运行该用例请确保库存充足)
     */
    @Test
    public void createOrder2() {
        String nowTime = DateTimeUtils.formatNow();
        Order order = new Order();
        order.setOrderId(System.currentTimeMillis());
        order.setOrderTime(nowTime);
        order.setOrderStatus(OrderStatusEnum.WPAY);
        order.setTotalAmount(0L);
        order.setTotalFreight(0L);
        order.setUserId(1L);
        order.setCreateTime(nowTime);

        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(createOrderLine(10047932928981L, 2));
        orderLines.add(createOrderLine(100028056874L, 2));
        order.setOrderLines(orderLines);
        orderAppService.createOrder2(order);
    }

    protected OrderLine createOrderLine(Long productId, int quantity) {
        Product product = productService.getProductById(productId);
        OrderLine orderLine = new OrderLine();
        orderLine.setProductId(productId);
        orderLine.setProductName(product.getProductName());
        orderLine.setProductUrl(product.getProductUrl());
        orderLine.setUnitPrice(product.getUnitPrice());
        orderLine.setQuantity(quantity);
        orderLine.setSubTotalAmount(0L);
        orderLine.setFreight(0L);
        return orderLine;
    }

}
