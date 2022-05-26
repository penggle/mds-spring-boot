-- 订单模块既分库又分表
CREATE TABLE t_order_0 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    user_id bigint unsigned NOT NULL COMMENT '用户ID',
    total_amount bigint unsigned NOT NULL COMMENT '总金额',
    total_freight bigint unsigned NOT NULL COMMENT '总运费',
    order_time datetime NOT NULL COMMENT '下单时间',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id)
) ENGINE=InnoDB COMMENT='订单表';

CREATE TABLE t_order_1 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    user_id bigint unsigned NOT NULL COMMENT '用户ID',
    total_amount bigint unsigned NOT NULL COMMENT '总金额',
    total_freight bigint unsigned NOT NULL COMMENT '总运费',
    order_time datetime NOT NULL COMMENT '下单时间',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id)
) ENGINE=InnoDB COMMENT='订单表';

CREATE TABLE t_order_2 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    user_id bigint unsigned NOT NULL COMMENT '用户ID',
    total_amount bigint unsigned NOT NULL COMMENT '总金额',
    total_freight bigint unsigned NOT NULL COMMENT '总运费',
    order_time datetime NOT NULL COMMENT '下单时间',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id)
) ENGINE=InnoDB COMMENT='订单表';

CREATE TABLE t_order_line_0 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    product_id bigint unsigned NOT NULL COMMENT '商品ID',
    product_name varchar(255) NOT NULL COMMENT '商品名称',
    product_url varchar(255) NOT NULL COMMENT '商品详情页URL',
    unit_price int unsigned NOT NULL COMMENT '商品单价',
    quantity int unsigned NOT NULL DEFAULT '1' COMMENT '购买数量',
    freight bigint unsigned NOT NULL DEFAULT '0' COMMENT '运费',
    sub_total_amount bigint unsigned NOT NULL COMMENT '小计金额',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    remark varchar(512) COMMENT '订单备注',
    order_time datetime NOT NULL COMMENT '下单时间',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id,product_id)
) ENGINE=InnoDB COMMENT='订单明细表';

CREATE TABLE t_order_line_1 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    product_id bigint unsigned NOT NULL COMMENT '商品ID',
    product_name varchar(255) NOT NULL COMMENT '商品名称',
    product_url varchar(255) NOT NULL COMMENT '商品详情页URL',
    unit_price int unsigned NOT NULL COMMENT '商品单价',
    quantity int unsigned NOT NULL DEFAULT '1' COMMENT '购买数量',
    freight bigint unsigned NOT NULL DEFAULT '0' COMMENT '运费',
    sub_total_amount bigint unsigned NOT NULL COMMENT '小计金额',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    remark varchar(512) COMMENT '订单备注',
    order_time datetime NOT NULL COMMENT '下单时间',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id,product_id)
) ENGINE=InnoDB COMMENT='订单明细表';

CREATE TABLE t_order_line_2 (
    order_id bigint unsigned NOT NULL COMMENT '订单ID',
    product_id bigint unsigned NOT NULL COMMENT '商品ID',
    product_name varchar(255) NOT NULL COMMENT '商品名称',
    product_url varchar(255) NOT NULL COMMENT '商品详情页URL',
    unit_price int unsigned NOT NULL COMMENT '商品单价',
    quantity int unsigned NOT NULL DEFAULT '1' COMMENT '购买数量',
    freight bigint unsigned NOT NULL DEFAULT '0' COMMENT '运费',
    sub_total_amount bigint unsigned NOT NULL COMMENT '小计金额',
    order_status varchar(32) NOT NULL DEFAULT 'WPAY' COMMENT '订单状态：WPAY,PAID,REFUND,CLOSED',
    remark varchar(512) COMMENT '订单备注',
    order_time datetime NOT NULL COMMENT '下单时间',
    create_time datetime NOT NULL COMMENT '创建时间',
    update_time datetime NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (order_id,product_id)
) ENGINE=InnoDB COMMENT='订单明细表';


-- 商品模块不分库也不分表
CREATE TABLE t_product (
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    product_name VARCHAR(191) NOT NULL COMMENT '商品名称',
    product_url VARCHAR(255) NOT NULL COMMENT '商品URL',
    product_type TINYINT(1) NOT NULL DEFAULT '1' COMMENT '商品类型：0-虚拟商品,1-实物商品',
    unit_price INT UNSIGNED NOT NULL COMMENT '商品单价',
    inventory INT UNSIGNED NOT NULL COMMENT '商品库存',
    remark VARCHAR(255) NULL DEFAULT NULL COMMENT '商品备注',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '最近修改时间',
    PRIMARY KEY (product_id)
) COMMENT='商品基础信息表' ENGINE=InnoDB;