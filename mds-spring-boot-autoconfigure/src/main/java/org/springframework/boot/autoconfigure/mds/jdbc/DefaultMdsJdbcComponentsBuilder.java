package org.springframework.boot.autoconfigure.mds.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * 默认的DalJdbcComponentsBuilder实现(连接池基于HikariDataSource)
 *
 * @author pengpeng
 * @since 2.1
 */
public class DefaultMdsJdbcComponentsBuilder extends MdsJdbcComponentsBuilder {

    @Override
    public DataSource buildDataSource(String database, DataSourceProperties properties) {
        if(StringUtils.hasText(properties.getJndiName())) { //JNDI数据源?
            return buildJndiDataSource(database, properties);
        } else {
            return buildPooledDataSource(database, properties);
        }
    }

    protected DataSource buildPooledDataSource(String database, DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
        dataSource.setPoolName(database + "HikariPool");
        return dataSource;
    }

    protected DataSource buildJndiDataSource(String database, DataSourceProperties properties) {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        DataSource dataSource = dataSourceLookup.getDataSource(properties.getJndiName());
        excludeMBeanIfNecessary(dataSource, MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database));
        return dataSource;
    }

    @Override
    protected String getPoolTypeName(String database, DataSourceProperties properties) {
        return StringUtils.hasText(properties.getJndiName()) ? null : "hikari";
    }

}
