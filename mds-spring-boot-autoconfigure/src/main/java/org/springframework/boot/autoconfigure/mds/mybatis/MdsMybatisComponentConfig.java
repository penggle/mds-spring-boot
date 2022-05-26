package org.springframework.boot.autoconfigure.mds.mybatis;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.mds.MdsComponentConfig;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * MDS组件之Mybatis配置
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public enum MdsMybatisComponentConfig implements MdsComponentConfig {

    MYBATIS("mybatis", MybatisProperties.class) {
        @Override
        public MybatisProperties getBoundedConfigProperties(Environment environment, String database) {
            String configPrefix = getConfigPrefix() + (StringUtils.hasText(database) ? "." + database : ""); //mybatis配置可以不区分数据库
            MybatisProperties configProperties = bindConfigProperties(environment, configPrefix);
            if(StringUtils.hasText(configProperties.getConfigLocation())
                || !ObjectUtils.isEmpty(configProperties.getMapperLocations())
                || !CollectionUtils.isEmpty(configProperties.getConfigurationProperties())
                || StringUtils.hasText(configProperties.getTypeAliasesPackage())
                || configProperties.getTypeAliasesSuperType() != null
                || StringUtils.hasText(configProperties.getTypeHandlersPackage())
                || configProperties.getDefaultScriptingLanguageDriver() != null
                || configProperties.getExecutorType() != null
                || configProperties.getConfiguration() != null
                || configProperties.isCheckConfigLocation()) {
                return configProperties;
            }
            return null;
        }

        @Override
        public MybatisProperties newDefaultConfigProperties() {
            return new MybatisProperties();
        }
    };

    private final String configPrefix;

    private final Class<?> configPropertiesType;

    MdsMybatisComponentConfig(String configPrefix, Class<?> configPropertiesType) {
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
