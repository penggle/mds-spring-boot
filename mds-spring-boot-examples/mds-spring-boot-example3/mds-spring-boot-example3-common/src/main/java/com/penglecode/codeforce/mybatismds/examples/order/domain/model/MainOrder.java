package com.penglecode.codeforce.mybatismds.examples.order.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.penglecode.codeforce.common.domain.EntityObject;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;

/**
 * 主订单
 *
 * @author pengpeng
 * @since 2.1
 */
@TableName("t_order")
public class MainOrder implements EntityObject {

    /** 订单ID */
    @TableId(type=IdType.INPUT)
    private Long orderId;

    /** 用户ID */
    private Long userId;

    /** 总金额(单位分) */
    private Long totalAmount;

    /** 总运费(单位分) */
    private Long totalFreight;

    /** 下单时间 */
    private String orderTime;

    /** 订单状态 */
    private OrderStatusEnum orderStatus;

    /** 创建时间 */
    private String createTime;

    /** 最近修改时间 */
    private String updateTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(Long totalFreight) {
        this.totalFreight = totalFreight;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public Long identity() {
        return orderId;
    }

}
