package com.penglecode.codeforce.mybatismds.examples.product.domain.model;

import com.penglecode.codeforce.common.domain.EntityObject;

/**
 * 商品信息
 *
 * @author pengpeng
 * @since 2.1
 */
public class Product implements EntityObject {

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 商品URL */
    private String productUrl;

    /** 商品类型：0-虚拟商品,1-实物商品 */
    private Integer productType;

    /** 商品单价 */
    private Long unitPrice;

    /** 商品库存 */
    private Integer inventory;

    /** 商品备注 */
    private String remark;

    /** 创建时间 */
    private String createTime;

    /** 最近修改时间 */
    private String updateTime;

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

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return productId;
    }

}
