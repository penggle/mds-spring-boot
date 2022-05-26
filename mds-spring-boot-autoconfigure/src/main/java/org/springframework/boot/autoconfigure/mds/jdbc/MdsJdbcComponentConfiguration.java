package org.springframework.boot.autoconfigure.mds.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableMultiTransactionManagement;

/**
 * MDS之JDBC组件自动配置
 *
 * @author pengpeng
 * @since 2.1
 */
@Configuration
@EnableMultiTransactionManagement(proxyTargetClass=true)
public class MdsJdbcComponentConfiguration {

    /**
     * 构建DataSource的Builder
     * 此处默认实现DefaultMdsJdbcComponentsBuilder是基于HikariCP数据源的
     */
    @Bean
    @ConditionalOnMissingBean
    public MdsJdbcComponentsBuilder mdsJdbcComponentsBuilder() {
        return new DefaultMdsJdbcComponentsBuilder();
    }

    /**
     * MDS组件之Jdbc部分组件注册程序(默认支持的)
     * 动态向Spring上下文中注册多个数据源的DataSource、JdbcTemplate、TransactionManager等bean(beanName区分数据库)
     */
    @Bean
    public MdsJdbcComponentsRegistry mdsJdbcComponentAutoConfigurer(MdsJdbcComponentsBuilder mdsJdbcComponentsBuilder) {
        return new MdsJdbcComponentsRegistry(mdsJdbcComponentsBuilder);
    }

}
