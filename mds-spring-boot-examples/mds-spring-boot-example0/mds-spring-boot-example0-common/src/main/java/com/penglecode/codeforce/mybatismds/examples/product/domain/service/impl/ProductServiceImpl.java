package com.penglecode.codeforce.mybatismds.examples.product.domain.service.impl;

import com.penglecode.codeforce.common.util.DateTimeUtils;
import com.penglecode.codeforce.common.util.StringUtils;
import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import com.penglecode.codeforce.mybatismds.examples.product.domain.service.ProductService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Types;
import java.util.Optional;

/**
 * 商品领域服务实现
 *
 * @author pengpeng
 * @since 2.1
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {

    //product前缀是约定的区分数据源的JdbcTemplate-bean名称前缀
    @Resource(name="productJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(value="productTransactionManager", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void createProduct(Product product) {
        product.setCreateTime(StringUtils.defaultIfBlank(product.getCreateTime(), DateTimeUtils.formatNow()));
        product.setUpdateTime(StringUtils.defaultIfBlank(product.getUpdateTime(), product.getCreateTime()));
        String sql = "INSERT INTO t_product( product_id, product_name, product_url, product_type, unit_price, inventory, remark, create_time, update_time ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sql, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.BIGINT, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR);
        factory.setReturnGeneratedKeys(true);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = factory.newPreparedStatementCreator(new Object[] {product.getProductId(), product.getProductName(), product.getProductUrl(), product.getProductType(), product.getUnitPrice(), product.getInventory(), product.getRemark(), product.getCreateTime(), product.getUpdateTime()});
        jdbcTemplate.update(psc, keyHolder);
        Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).ifPresent(product::setProductId);
    }

    @Override
    public Product getProductById(Long productId) {
        String sql = "SELECT * FROM t_product a WHERE a.product_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Product.class), productId);
    }

    @Override
    @Transactional(transactionManager="productTransactionManager", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void decrProductInventory1(Long productId, int delta) {
        String sql = "UPDATE t_product SET inventory = inventory + ? WHERE product_id = ?";
        jdbcTemplate.update(sql, -delta, productId);
    }

    @Override
    @Transactional(transactionManager="productTransactionManager", propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public void decrProductInventory2(Long productId, int delta) {
        String sql = "UPDATE t_product SET inventory = inventory + ? WHERE product_id = ?";
        jdbcTemplate.update(sql, -delta, productId);
    }

    @Override
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    public void incrProductInventory(Long productId, int delta) {
        String sql = "UPDATE t_product SET inventory = inventory + ? WHERE product_id = ?";
        jdbcTemplate.update(sql, delta, productId);
    }

}
