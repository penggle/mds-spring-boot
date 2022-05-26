# mybatis-springboot-mds

## 基本简介

mybatis-springboot-mds是什么？

mybatis-springboot-mds是一个基于mybatis-springboot(`mybatis-spring-boot-starter`)集成底座的多数据源数据访问层组件，全场景支持。



## 特性及限制

支持基于mybatis-springboot(`mybatis-spring-boot-starter`)的全场景：
- ##### 支持Mybatis的原生用法
- ##### 支持MybatisPlus、MybatisTiny等懒人框架用法

- ##### 支持本地事务，不支持分布式事务

  - 【支持本地事务】即支持在同一个JVM中通过JDBC操作多个数据源(数据库)，保证其正确的Spring事务传播特性

- ##### 支持传统单库多数据源场景，这也是最常见的

- ##### 支持客户端分库分表的多数据源场景

  - 【客户端分库分表】即诸如ShardingSphere-JDBC这样的客户端(嵌入在应用内部的)分库分表框架
  - 这种情况下还支持混用，即存在单库数据源(例如项目中大多数表不需要分库分表，其不属于ShardingSphere管理，而属于应用本身来管理)，也存在ShardingSphereDataSource(比如项目中有几个表确实需借助ShardingSphere来做分库分表)，这种混用的场景在实际项目中也非常场景。[示例代码](https://github.com/penggle/mybatis-springboot-mds/tree/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-csmds)就是这种混用方式

- ##### 支持服务端分库分表多数据源场景

  - 【服务端分库分表】即诸如ShardingSphere-Proxy、Mycat这样的服务端(伪装成一个数据库Server)分库分表中间件
  - 这种情况下跟传统的单库多数据源场景一样了，因为作为应用的客户端看来，ShardingSphere-Proxy、Mycat这样的分库分表中间就是一个数据库Server

- ##### 再回头看看，是不是支持全场景?（除了不支持分布式事务）



## 快速入门

> **Talk is cheap，show me the code！**

- ##### 第一步引入依赖

  ```xml
  <dependency>
      <groupId>io.github.penggle</groupId>
      <artifactId>mybatis-springboot-mds-starter</artifactId>
      <!-- 版本说明：2.1指的是基于mybatis-spring-boot-starter 2.1.x版本的意思 -->
      <version>2.1</version>
  </dependency>
  
  <!-- 当然mybatis-spring-boot-starter也是需要手动引入的 -->
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.1.4</version>
  </dependency>
  ```

- 在SpringBoot启动类上启用多数据源支持

  ```java
  @SpringBootApplication
  @EnableMultiDataSource({@NamedDatabase("product"), @NamedDatabase("order")})
  public class GmdsExample1Application {
  
      public static void main(String[] args) {
          SpringApplication.run(GmdsExample1Application.class, args);
      }
  
  }
  ```

- XxxMapper接口上标记所属数据源

  ```java
  @NamedDatabase("product")
  public interface ProductMapper {
  	...
  }
  
  @NamedDatabase("order")
  public interface MainOrderMapper {
  	...
  }
  
  @NamedDatabase("order")
  public interface OrderLineMapper {
  	...
  }
  ```
  
- application.yml配置

  - springboot-datasource配置（**必要配置**）

    ```yaml
    spring:
        #数据源配置
        datasource:
            #公共连接池配置
            hikari:
                #最小空闲连接数量
                minimum-idle: 5
                #空闲连接存活最大时间，默认600000(10分钟)
                idle-timeout: 180000
                #连接池最大连接数，默认是10
                maximum-pool-size: 10
                #池中连接的默认自动提交行为，默认值true
                auto-commit: true
                #池中连接的最长生命周期，0表示无限生命周期，默认1800000(30分钟)
                max-lifetime: 1800000
                #等待来自池的连接的最大毫秒数，默认30000(30秒)
                connection-timeout: 30000
                #连接测试语句
                connection-test-query: SELECT 1
            #商品库配置(逻辑名称)
            product:
                username: root
                password: 123456
                url: jdbc:mysql://127.0.0.1:3306/ec_product?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&rewriteBatchedStatements=true&useCursorFetch=true
            #订单库配置(逻辑名称)
            order:
                username: root
                password: 123456
                url: jdbc:mysql://127.0.0.1:3306/ec_order?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false&rewriteBatchedStatements=true&useCursorFetch=true
    ```

  - mybatis-spring-boot-starter模块配置（**可选配置**）

    ```yaml
    #mybatis-springboot配置
    mybatis:
        config-location: classpath:config/mybatis/mybatis-config.xml
        mapper-locations: classpath*:com/penglecode/codeforce/mybatismds/examples/**/*Mapper.xml
        type-aliases-package: com.penglecode.codeforce.mybatismds.examples
        type-aliases-super-type: com.penglecode.codeforce.common.domain.DomainObject
        #商品库的Mybatis特殊配置
        product:
            config-location: classpath:config/mybatis/mybatis-config.xml
            mapper-locations: classpath*:com/penglecode/codeforce/mybatismds/examples/product/**/*Mapper.xml
            type-aliases-package: com.penglecode.codeforce.mybatismds.examples
            type-aliases-super-type: com.penglecode.codeforce.common.domain.DomainObject
    ```

- 好了，配置完毕，启动项目后，自动注册如下bean到Spring应用上下文中去：

  - {database}DataSourceProperties：指定数据库的`org.springframework.boot.autoconfigure.jdbc.DataSourceProperties`
  - {database}DataSource：指定数据库的`javax.sql.DataSource`
  - {database}JdbcTemplate：指定数据库的`org.springframework.jdbc.core.JdbcTemplate`
  - {database}TransactionManager：指定数据库的`org.springframework.transaction.PlatformTransactionManager`
  - {database}SqlSessionFactory：指定数据库的`org.apache.ibatis.session.SqlSessionFactory`
  - {database}SqlSessionTemplate：指定数据库的`org.mybatis.spring.SqlSessionTemplate`
  - {database}XxxMapper：指定数据库的各个实体的XxxMapper接口代理
  - allTransactionManager：默认的全局多数据源事务管理器`org.springframework.data.transaction.ChainedTransactionManager`

- 本地事务使用示例：

  碍于篇幅使用精简代码，具体代码见[示例代码](https://github.com/penggle/mybatis-springboot-mds/tree/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-gmds)

  - 测试`Propagation.REQUIRED`传播特性（[示例代码](https://github.com/penggle/mybatis-springboot-mds/blob/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-common/src/main/java/com/penglecode/codeforce/mybatismds/examples/order/app/service/impl/OrderAppServiceImpl.java)）

    > 由于商品表的库存字段是UNSIGNED，多运行几次会导致扣库存失败，此时可以看看商品表与订单表是否全部回滚了事务

    ```java
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(transactionManager="productTransactionManager,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(transactionManager="all", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //在本例中以上几种@Transactional的写法都是等效的，但是建议：用到那几个库就指明其事务管理器的名称或别名
    public void createOrder1(Order order) {
        initOrder(order);
        mainOrderService.createMainOrder(order); //创建主订单
        orderLineService.createOrderLines(order.getOrderLines()); //创建订单明细
        for(OrderLine orderLine : order.getOrderLines()) {
            //decrProductInventory1()方法上设置的事务传播特性是Propagation.REQUIRED
            productService.decrProductInventory1(orderLine.getProductId(), orderLine.getQuantity()); //扣库存
        }
    }
    ```

    

  - 测试`Propagation.REQUIRES_NEW`传播特性（[示例代码](https://github.com/penggle/mybatis-springboot-mds/blob/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-common/src/main/java/com/penglecode/codeforce/mybatismds/examples/order/app/service/impl/OrderAppServiceImpl.java)）

    ```java
    @Transactional(transactionManager="product,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(transactionManager="productTransactionManager,order", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(transactionManager="all", propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    //在本例中以上几种@Transactional的写法都是等效的，但是建议：用到那几个库就指明其事务管理器的名称或别名
    public void createOrder2(Order order) {
        initOrder(order);
        mainOrderService.createMainOrder(order); //创建主订单
        orderLineService.createOrderLines(order.getOrderLines()); //创建订单明细
        for(OrderLine orderLine : order.getOrderLines()) {
            //decrProductInventory2()方法上设置的事务传播特性是Propagation.REQUIRES_NEW
            productService.decrProductInventory2(orderLine.getProductId(), orderLine.getQuantity()); //扣库存
        }
        Assert.isTrue(order.getOrderId().equals(System.currentTimeMillis()), "我不是故意的：测试Propagation.REQUIRES_NEW");
    }
    ```



## 使用示例

- ##### 动态注册生命周期

  mybatis-springboot-mds框架提供了一个钩子接口：[MdsComponentsRegistrationLifecycle](https://github.com/penggle/mybatis-springboot-mds/blob/main/mybatis-springboot-mds-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/mybatis/MdsComponentsRegistrationLifecycle.java)

  ```java
  public interface MdsComponentsRegistrationLifecycle {
  
      /**
       * 在动态注册MDS组件之前做一些事件
       *
       * @param mdsAnnotationMetadata     - 可以取到{@link EnableMultiDataSource}注解的元数据信息
       * @param registry                  - Spring Bean注册器
       */
      default void beforeMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {
  
      }
  
      /**
       * 在MDS之JDBC组件(DataSource、JdbcTemplate、TransactionManager)动态注册完毕之后做一些事情
       *
       * @param mdsAnnotationMetadata
       * @param registry
       * @param mdsComponentBeans
       */
      default void onMdsJdbcComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
  
      }
  
      /**
       * 在MDS之Mybatis组件(SqlSessionFactory、SqlSessionTemplate、XxxMapper)动态注册完毕之后做一些事情
       *
       * @param mdsAnnotationMetadata
       * @param registry
       * @param mdsComponentBeans
       */
      default void onMdsMybatisComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
  
      }
  
      /**
       * 在所有MDS组件动态注册完毕之后做一些事情
       *
       * @param mdsAnnotationMetadata     - 可以取到{@link EnableMultiDataSource}注解的元数据信息
       * @param registry                  - Spring Bean注册器
       * @param mdsComponentBeans         - 已动态注册的MDS组件
       */
      default void afterMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
  
      }
  
  }
  ```

  基于它，我们能干很多事情，我们可以自定义一个实现，并注册到Spring上下文中，这在遇到外部数据源的时候特别有用，例如"[基于ShardingSphere-JDBC的示例](https://github.com/penggle/mybatis-springboot-mds/blob/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-csmds/src/main/java/com/penglecode/codeforce/mybatismds/examples/common/config/ShardingSphereConfiguration.java)"

- ##### 外部数据源支持

  什么叫外部数据源？即数据源不是通过约定的基于如下多数据源配置自动构建出来的情况：

  ```yaml
  spring:
      #db1库配置(逻辑名称)
      db1:
          username: root
          password: 123456
          url: jdbc:mysql://127.0.0.1:3306/myapp_db1
  ```

  ```java
  @SpringBootApplication
  @EnableMultiDataSource({@NamedDatabase("db1"), @NamedDatabase("db2")})
  public class MyApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(MyApplication.class, args);
      }
  
  }
  ```

  <u>此时基于约定的配置，并没有发现db2的yaml配置，此时mybatis-springboot-mds框架会去Spring上下文中找名字为db2DataSource的bean，如果没有则会报错。此时db2就称之为**外部数据源**</u>

  具体请看下面的基于ShardingSphere-JDBC的示例

- ##### 基于ShardingSphere-JDBC的示例

  首先为了兼顾灵活性mybatis-springboot-mds框架内部并没有任何特殊处理ShardingSphere的地方，对于mybatis-springboot-mds框架来说，ShardingSphereDataSource实例是一个**外部数据源**，仅要求其在运行时存在于Spring上下文中即可。

  mybatis-springboot-mds框架动态注册数据访问层bean的时机太早了，以至于ShardingSphere的自动配置类`org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration`、`org.apache.shardingsphere.sharding.spring.boot.ShardingRuleSpringBootConfiguration`都还没注册到Spring上下文中，此时通过实现MdsComponentsRegistrationLifecycle来在Spring启动的早期阶段勾起对上面两个配置类的加载与自动配置工作。这个里面水很深，具体可见[基于ShardingSphere-JDBC的示例](https://github.com/penggle/mybatis-springboot-mds/blob/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example1/mybatis-springboot-mds-example1-csmds/src/main/java/com/penglecode/codeforce/mybatismds/examples/common/config/ShardingSphereConfiguration.java)

- ##### 基于MybatisTiny的示例

  [MybatisTiny](https://github.com/penggle/mybatis-tiny)是一个与MybatisPlus类似的框架，基于MybatisTiny的示例见[这里](https://github.com/penggle/mybatis-springboot-mds/tree/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example2)

- ##### 基于MybatisPlus的示例

  基于MybatisPlus的示例见[这里](https://github.com/penggle/mybatis-springboot-mds/tree/main/mybatis-springboot-mds-examples/mybatis-springboot-mds-example3)

- ##### 示例所使用的数据库及表结构

  所有示例所使用的数据库均为MySQL数据库，表结构见[这里](https://github.com/penggle/mybatis-springboot-mds/tree/main/mybatis-springboot-mds-examples/schemas)
