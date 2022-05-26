package com.penglecode.codeforce.mybatismds.examples.order.dal.mapper;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.OrderLine;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("order")
public interface OrderLineMapper {

    int insert(OrderLine entity);

    int updateById(@Param("id") Serializable id, @Param("columns") Map<String,Object> columns);

    int deleteById(@Param("id") Serializable id);

    OrderLine selectById(@Param("id") Serializable id);

    List<OrderLine> selectListByOrderId(@Param("orderId") Long orderId);

}
