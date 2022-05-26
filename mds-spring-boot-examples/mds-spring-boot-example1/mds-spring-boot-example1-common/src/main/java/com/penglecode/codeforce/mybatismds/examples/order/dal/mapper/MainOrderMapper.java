package com.penglecode.codeforce.mybatismds.examples.order.dal.mapper;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
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
public interface MainOrderMapper {

    int insert(MainOrder entity);

    int updateById(@Param("id") Serializable id, @Param("columns") Map<String,Object> columns);

    int deleteById(@Param("id") Serializable id);

    MainOrder selectById(@Param("id") Serializable id);

    List<MainOrder> selectAllList();

    int selectAllCount();

}
