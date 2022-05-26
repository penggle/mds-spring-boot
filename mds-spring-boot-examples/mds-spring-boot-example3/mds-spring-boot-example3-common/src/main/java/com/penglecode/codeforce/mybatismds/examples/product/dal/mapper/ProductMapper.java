package com.penglecode.codeforce.mybatismds.examples.product.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.penglecode.codeforce.mybatismds.examples.product.domain.model.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

import java.io.Serializable;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("product")
public interface ProductMapper extends BaseMapper<Product> {

    int updateInventory(@Param("id") Serializable id, @Param("delta") Integer delta);

}
