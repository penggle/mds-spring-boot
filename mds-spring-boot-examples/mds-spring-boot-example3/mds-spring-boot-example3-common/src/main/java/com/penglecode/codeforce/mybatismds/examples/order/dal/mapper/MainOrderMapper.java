package com.penglecode.codeforce.mybatismds.examples.order.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.penglecode.codeforce.mybatismds.examples.order.domain.model.MainOrder;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

/**
 * @author pengpeng
 * @since 2.1
 */
@NamedDatabase("order")
public interface MainOrderMapper extends BaseMapper<MainOrder> {

}
