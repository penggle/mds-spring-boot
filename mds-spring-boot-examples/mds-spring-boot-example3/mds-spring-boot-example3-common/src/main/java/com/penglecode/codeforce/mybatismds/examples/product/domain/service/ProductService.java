package com.penglecode.codeforce.mybatismds.examples.product.domain.service;

import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;

/**
 * 商品领域服务接口
 *
 * @author pengpeng
 * @since 2.1
 */
public interface ProductService {

    /**
     * 创建商品
     *
     * @param product
     */
    void createProduct(Product product);

    /**
     * 根据商品ID获取商品
     *
     * @param productId
     * @return
     */
    Product getProductById(Long productId);

    /**
     * 减库存
     *
     * @param productId
     * @param delta
     */
    void decrProductInventory1(Long productId, int delta);

    /**
     * 减库存
     *
     * @param productId
     * @param delta
     */
    void decrProductInventory2(Long productId, int delta);

    /**
     * 增库存
     *
     * @param productId
     * @param delta
     */
    void incrProductInventory(Long productId, int delta);

}
