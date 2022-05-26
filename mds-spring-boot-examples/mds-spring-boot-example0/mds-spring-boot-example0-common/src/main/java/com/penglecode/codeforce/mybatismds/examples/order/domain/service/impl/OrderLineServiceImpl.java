package com.penglecode.codeforce.mybatismds.examples.order.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.OrderLineService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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

    //order前缀是约定的区分数据源的JdbcTemplate-bean名称前缀
    @Resource(name="orderJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createOrderLines(List<OrderLine> orderLines) {
        for(OrderLine orderLine : orderLines) {
            orderLine.setOrderStatus(OrderStatusEnum.WPAY);
            orderLine.setCreateTime(StringUtils.defaultIfBlank(orderLine.getCreateTime(), DateTimeUtils.formatNow()));
            orderLine.setUpdateTime(StringUtils.defaultIfBlank(orderLine.getUpdateTime(), orderLine.getCreateTime()));
            String sql = "INSERT INTO t_order_line ( order_id, product_id, product_name, product_url, unit_price, quantity, freight, sub_total_amount, order_time, order_status, remark, create_time, update_time ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
            jdbcTemplate.update(sql, orderLine.getOrderId(), orderLine.getProductId(), orderLine.getProductName(), orderLine.getProductUrl(), orderLine.getUnitPrice(), orderLine.getQuantity(), orderLine.getFreight(), orderLine.getSubTotalAmount(), orderLine.getOrderTime(), orderLine.getOrderStatus().toString(), orderLine.getRemark(), orderLine.getCreateTime(), orderLine.getUpdateTime());
        }
    }

    @Override
    public List<OrderLine> getOrderLinesByOrderId(Long orderId) {
        String sql = "SELECT * FROM t_order_line a WHERE a.order_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderLine.class), orderId);
    }

}
