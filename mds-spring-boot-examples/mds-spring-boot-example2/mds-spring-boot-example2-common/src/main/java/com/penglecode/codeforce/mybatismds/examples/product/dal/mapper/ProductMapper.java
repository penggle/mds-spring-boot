package com.penglecode.codeforce.mybatismds.examples.product.dal.mapper;

import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import com.penglecode.codeforce.mybatistiny.mapper.BaseEntityMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

import java.io.Serializable;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("product")
public interface ProductMapper extends BaseEntityMapper<Product> {

    int updateInventory(@Param("id") Serializable id, @Param("delta") Integer delta);

}
