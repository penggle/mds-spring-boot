package com.penglecode.codeforce.mybatismds.examples.order.dal.mapper;

import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
import com.penglecode.codeforce.mybatistiny.mapper.BaseEntityMapper;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("order")
public interface MainOrderMapper extends BaseEntityMapper<MainOrder> {

}
