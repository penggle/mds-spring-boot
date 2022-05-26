package org.springframework.boot.autoconfigure.mds;

import org.springframework.boot.autoconfigure.mds.jdbc.MdsJdbcComponentConfiguration;
import org.springframework.boot.autoconfigure.mds.mybatis.MdsMybatisComponentConfiguration;
import org.springframework.boot.autoconfigure.mds.mybatis.MybatisExtensionConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用基于mybatis-springboot的多数据源数据库访问层自动配置
 *
 * <p>
 * 配置示例：
 * <br/>
 * application.yml:
 * <br/>
 * <pre class="code">
 *
 * spring:
 *     #数据源连接池配置(若只有一个数据库则下面只保留default部分即可)
 *     datasource:
 *         #默认库(逻辑名称)
 *         default:
 *             username: root
 *             password: 123456
 *             url: jdbc:mysql://127.0.0.1:3306/ec_commons?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&rewriteBatchedStatements=true
 *         #商品库(逻辑名称)
 *         product:
 *             username: root
 *             password: 123456
 *             url: jdbc:mysql://127.0.0.1:3306/ec_product?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&rewriteBatchedStatements=true
 *         #订单库(逻辑名称)
 *         order:
 *             username: root
 *             password: 123456
 *             url: jdbc:mysql://127.0.0.1:3306/ec_order?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&rewriteBatchedStatements=true
 * #MyBatis配置
 * mybatis:
 *     config-location: classpath:config/mybatis/mybatis-config.xml
 *     mapper-locations: classpath*:com/xxx/myapp/**\/*Mapper.xml
 *     type-aliases-package:com.xxx.myapp
 *     #商品库的Mybatis特殊配置(如果需要的话)
 *     product:
 *         config-location: classpath:config/mybatis/mybatis-config.xml
 *         mapper-locations: classpath*:com/xxx/myapp/product/**\/*Mapper.xml
 *         type-aliases-package:com.xxx.myapp.product
 * </pre>
 *
 * 项目启动的早期阶段会默认动态注册beanName以指定database(逻辑名称)前缀的Bean进入Spring应用上下文中，被自动注册的诸如以下bean：
 * <br/>
 * 1、各个database的：{database}DataSourceProperties、{database}MybatisProperties、{database}DataSource、{database}JdbcTemplate、{database}TransactionManager、{database}SqlSessionFactory、{database}SqlSessionTemplate、{database}XxxMapper
 * <br/>
 * 2、默认的全局多数据源事务管理器：allTransactionManager (固定名称)
 * <br/>
 *
 * 支持本地事务，例如基于Spring的声明式事务用法：
 *
 * <br/>
 * <pre class="code">
 *
 *     &#064;Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
 *     //&#064;Transactional(transactionManager="productTransactionManager,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
 *     //&#064;Transactional(transactionManager="all", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
 *     //&#064;Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
 *     //在本例中以上几种&#064;Transactional的写法都是等效的，但是建议：用到那几个库就指明其事务管理器的名称或别名
 *     public void createOrder(Order order) {
 *          createMainOrder(order); //创建主订单
 *          for(OrderLine orderLine : order.getOrderLines()) {
 *              createOrderLine(orderLine); //创建子订单
 *              decrProductInventory(orderLine.getProductId(), orderLine.getQuantity()); //扣减库存
 *          }
 *     }
 *
 * </pre>
 * </p>
 *
 * @author pengpeng
 * @since 2.1
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MdsComponentRegistrar.class, MdsJdbcComponentConfiguration.class, MybatisExtensionConfiguration.class, MdsMybatisComponentConfiguration.class})
public @interface EnableMultiDataSource {

    /**
     * 激活自动注册多数据源组件的数据库名称，
     * 注意：如果配置文件(application.yaml|properties)中存在相关多数据库的配置，但是此处没有指定则不会自动注册其数据访问层组件的
     * @return
     */
    NamedDatabase[] value();

}
