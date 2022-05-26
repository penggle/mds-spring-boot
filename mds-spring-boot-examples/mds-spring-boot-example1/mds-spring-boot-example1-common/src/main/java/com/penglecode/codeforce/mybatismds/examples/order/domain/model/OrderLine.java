package com.penglecode.codeforce.mybatismds.examples.order.domain.model;

import com.penglecode.codeforce.common.domain.EntityObject;
import com.penglecode.codeforce.common.domain.ID;
import com.penglecode.codeforce.mybatismds.examples.order.domain.enums.OrderStatusEnum;

/**
 * 订单明细
 *
 * @author pengpeng
 * @since 2.1
 */
public class OrderLine implements EntityObject {

    /** 订单ID */
    private Long orderId;

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 商品详情页URL */
    private String productUrl;

    /** 商品单价(单位分) */
    private Long unitPrice;

    /** 购买数量 */
    private Integer quantity;

    /** 运费 */
    private Long freight;

    /** 小计(单位分) */
    private Long subTotalAmount;

    /** 订单状态 */
    private OrderStatusEnum orderStatus;

    /** 订单备注 */
    private String remark;

    /** 下单时间 */
    private String orderTime;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getFreight() {
        return freight;
    }

    public void setFreight(Long freight) {
        this.freight = freight;
    }

    public Long getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(Long subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
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
    public ID identity() {
        return new ID().addKey("orderId", orderId).addKey("productId", productId);
    }

}
