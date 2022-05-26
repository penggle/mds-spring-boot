package com.penglecode.codeforce.mybatismds.examples.order.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.order.dal.mapper.MainOrderMapper;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
import com.penglecode.codeforce.mybatismds.examples.order.domain.service.MainOrderService;
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

    //order前缀是约定的区分数据源的Mapper-bean名称前缀
    @Resource(name="orderMainOrderMapper")
    private MainOrderMapper mainOrderMapper;

    @Override
    public void createMainOrder(MainOrder mainOrder) {
        mainOrder.setOrderStatus(OrderStatusEnum.WPAY);
        mainOrder.setCreateTime(StringUtils.defaultIfBlank(mainOrder.getCreateTime(), DateTimeUtils.formatNow()));
        mainOrder.setUpdateTime(StringUtils.defaultIfBlank(mainOrder.getUpdateTime(), mainOrder.getCreateTime()));
        mainOrderMapper.insert(mainOrder);
    }

    @Override
    public MainOrder getMainOrderById(Long orderId) {
        return mainOrderMapper.selectById(orderId);
    }

}
