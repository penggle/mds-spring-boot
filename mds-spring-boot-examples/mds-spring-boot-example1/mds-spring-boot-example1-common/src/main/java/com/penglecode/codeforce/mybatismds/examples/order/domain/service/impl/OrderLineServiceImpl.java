package com.penglecode.codeforce.mybatismds.examples.order.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.order.dal.mapper.OrderLineMapper;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.OrderLineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单明细领域服务实现
 *
 * @author pengpeng
 * @since 2.1
 */
@Service("orderLineService")
public class OrderLineServiceImpl implements OrderLineService {

    //order前缀是约定的区分数据源的Mapper-bean名称前缀
    @Resource(name="orderOrderLineMapper")
    private OrderLineMapper orderLineMapper;

    @Override
    public void createOrderLines(List<OrderLine> orderLines) {
        for(OrderLine orderLine : orderLines) {
            orderLine.setOrderStatus(OrderStatusEnum.WPAY);
            orderLine.setCreateTime(StringUtils.defaultIfBlank(orderLine.getCreateTime(), DateTimeUtils.formatNow()));
            orderLine.setUpdateTime(StringUtils.defaultIfBlank(orderLine.getUpdateTime(), orderLine.getCreateTime()));
            orderLineMapper.insert(orderLine);
        }
    }

    @Override
    public List<OrderLine> getOrderLinesByOrderId(Long orderId) {
        return orderLineMapper.selectListByOrderId(orderId);
    }

}
