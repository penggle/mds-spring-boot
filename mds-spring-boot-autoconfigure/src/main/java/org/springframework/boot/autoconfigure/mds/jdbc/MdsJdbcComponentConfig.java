package org.springframework.boot.autoconfigure.mds.jdbc;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.mds.MdsComponentConfig;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * MDS组件之JDBC配置
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public enum MdsJdbcComponentConfig implements MdsComponentConfig {

    DATASOURCE("spring.datasource", DataSourceProperties.class) {
        @Override
        public DataSourceProperties getBoundedConfigProperties(Environment environment, String database) {
            if(StringUtils.hasText(database)) {
                String configPrefix = getConfigPrefix() + "." + database; //数据源配置必须区分数据库
                DataSourceProperties configProperties = bindConfigProperties(environment, configPrefix);
                if(StringUtils.hasText(configProperties.getJndiName())
                    || (StringUtils.hasText(configProperties.getUrl()) && StringUtils.hasText(configProperties.getUsername()) && StringUtils.hasText(configProperties.getPassword()))) {
                    return configProperties;
                }
            }
            return null;
        }

        @Override
        public DataSourceProperties newDefaultConfigProperties() {
            return new DataSourceProperties();
        }
    },

    JDBC("spring.jdbc", JdbcProperties.class) {
        @Override
        public JdbcProperties getBoundedConfigProperties(Environment environment, String database) {
            return bindConfigProperties(environment, getConfigPrefix());
        }

        @Override
        public JdbcProperties newDefaultConfigProperties() {
            return new JdbcProperties();
        }
    };

    private final String configPrefix;

    private final Class<?> configPropertiesType;

    MdsJdbcComponentConfig(String configPrefix, Class<?> configPropertiesType) {
        this.configPrefix = configPrefix;
        this.configPropertiesType = configPropertiesType;
    }

    public String getConfigPrefix() {
        return configPrefix;
    }

    public Class<?> getConfigPropertiesType() {
        return configPropertiesType;
    }

}
