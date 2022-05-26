package com.penglecode.codeforce.mybatismds.examples.order.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.MainOrderService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 主订单领域服务实现
 *
 * @author pengpeng
 * @since 2.1
 */
@Service("mainOrderService")
public class MainOrderServiceImpl implements MainOrderService {

    //order前缀是约定的区分数据源的JdbcTemplate-bean名称前缀
    @Resource(name="orderJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createMainOrder(MainOrder mainOrder) {
        mainOrder.setOrderStatus(OrderStatusEnum.WPAY);
        mainOrder.setCreateTime(StringUtils.defaultIfBlank(mainOrder.getCreateTime(), DateTimeUtils.formatNow()));
        mainOrder.setUpdateTime(StringUtils.defaultIfBlank(mainOrder.getUpdateTime(), mainOrder.getCreateTime()));
        String sql = "INSERT INTO t_order ( order_id, user_id, total_amount, total_freight, order_time, order_status, create_time, update_time ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
        jdbcTemplate.update(sql, mainOrder.getOrderId(), mainOrder.getUserId(), mainOrder.getTotalAmount(), mainOrder.getTotalFreight(), mainOrder.getOrderTime(), mainOrder.getOrderStatus().toString(), mainOrder.getCreateTime(), mainOrder.getUpdateTime());
    }

    @Override
    public MainOrder getMainOrderById(Long orderId) {
        String sql = "SELECT * FROM t_order a WHERE a.order_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MainOrder.class), orderId);
    }

}
