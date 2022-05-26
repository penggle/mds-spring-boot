package org.springframework.boot.autoconfigure.mds.jdbc;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.mds.MdsComponentsBuilderSupport;

import javax.sql.DataSource;

/**
 * MDS组件之JDBC部分的Builder接口
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public abstract class MdsJdbcComponentsBuilder extends MdsComponentsBuilderSupport {

    /**
     * 创建DataSource
     *
     * @param database
     * @param properties
     * @return
     */
    public abstract DataSource buildDataSource(String database, DataSourceProperties properties);

    /**
     * 获取数据库连接池类型名称，例如："hikari"，"dbcp2"等
     *
     * @param database
     * @param properties
     * @return
     */
    protected abstract String getPoolTypeName(String database, DataSourceProperties properties);

    /**
     * Copied from org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration
     */
    protected <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
        return (T) properties.initializeDataSourceBuilder().type(type).build();
    }

}
