package com.penglecode.codeforce.mybatismds.examples.product.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.product.dal.mapper.ProductMapper;
import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import com.penglecode.codeforce.mybatismds.examples.product.domain.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 商品领域服务实现
 *
 * @author pengpeng
 * @since 2.1
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {

    //product前缀是约定的区分数据源的Mapper-bean名称前缀
    @Resource(name="productProductMapper")
    private ProductMapper productMapper;

    @Override
    @Transactional(value="productTransactionManager", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void createProduct(Product product) {
        product.setCreateTime(StringUtils.defaultIfBlank(product.getCreateTime(), DateTimeUtils.formatNow()));
        product.setUpdateTime(StringUtils.defaultIfBlank(product.getUpdateTime(), product.getCreateTime()));
        productMapper.insert(product);
    }

    @Override
    public Product getProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional(transactionManager="productTransactionManager", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void decrProductInventory1(Long productId, int delta) {
        productMapper.updateInventory(productId, -delta);
    }

    @Override
    @Transactional(transactionManager="productTransactionManager", propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void decrProductInventory2(Long productId, int delta) {
        productMapper.updateInventory(productId, -delta);
    }

    @Override
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void incrProductInventory(Long productId, int delta) {
        productMapper.updateInventory(productId, delta);
    }

}
