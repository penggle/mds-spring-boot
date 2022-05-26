package org.springframework.boot.autoconfigure.mds.jdbc;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.mds.MdsComponentType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * MDS之JDBC组件类型枚举
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public enum MdsJdbcComponentType implements MdsComponentType {

    DATASOURCE_PROPERTIES(DataSourceProperties.class),
    DATASOURCE(DataSource.class),
    JDBC_TEMPLATE(JdbcTemplate.class),
    TRANSACTION_MANAGER(DataSourceTransactionManager.class) {
        @Override
        public String getComponentBeanName(String database) {
            return MdsComponentType.getComponentBeanName(database, TransactionManager.class);
        }
    };

    private final Class<?> componentBeanType;

    MdsJdbcComponentType(Class<?> componentBeanType) {
        this.componentBeanType = componentBeanType;
    }

    @Override
    public <T> Class<T> getComponentBeanType() {
        return (Class<T>) componentBeanType;
    }

}
