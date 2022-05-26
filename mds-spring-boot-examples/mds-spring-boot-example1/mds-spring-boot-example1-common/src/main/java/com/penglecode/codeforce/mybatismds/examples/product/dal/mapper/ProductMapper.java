package com.penglecode.codeforce.mybatismds.examples.product.dal.mapper;

import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("product")
public interface ProductMapper {

    int insert(Product entity);

    int updateById(@Param("id") Serializable id, @Param("columns") Map<String,Object> columns);

    int updateInventory(@Param("id") Serializable id, @Param("delta") Integer delta);

    int deleteById(@Param("id") Serializable id);

    Product selectById(@Param("id") Serializable id);

    List<Product> selectAllList();

    int selectAllCount();

}
