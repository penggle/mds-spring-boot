package org.springframework.boot.autoconfigure.mds.mybatis;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MdsMybatisComponentsBuilder默认实现
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("rawtypes")
public class DefaultMdsMybatisComponentsBuilder extends MdsMybatisComponentsBuilder {

    @Override
    public FactoryBean<SqlSessionFactory> buildSqlSessionFactoryBean(String database, DataSource dataSource, MybatisProperties properties) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        applyConfiguration(sqlSessionFactoryBean, database, properties);
        List<Interceptor> interceptors = getAgreedNamingBeans(database, Interceptor.class);
        if(!CollectionUtils.isEmpty(interceptors)) {
            sqlSessionFactoryBean.setPlugins(interceptors.toArray(new Interceptor[0]));
        }
        DatabaseIdProvider databaseIdProvider = getAgreedNamingBean(database, DatabaseIdProvider.class);
        if(databaseIdProvider != null) {
            sqlSessionFactoryBean.setDatabaseIdProvider(databaseIdProvider);
        }
        if(StringUtils.hasLength(properties.getTypeAliasesPackage())) {
            sqlSessionFactoryBean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        if(properties.getTypeAliasesSuperType() != null) {
            sqlSessionFactoryBean.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
        }
        if(StringUtils.hasLength(properties.getTypeHandlersPackage())) {
            sqlSessionFactoryBean.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        }
        List<TypeHandler> typeHandlers = getBeanFactory().getBeanProvider(TypeHandler.class).orderedStream().collect(Collectors.toList());
        if(!ObjectUtils.isEmpty(typeHandlers)) {
            sqlSessionFactoryBean.setTypeHandlers(typeHandlers.toArray(new TypeHandler[0]));
        }
        if(!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
            //若mapperLocations区分不了数据库,不过这不是事,因为下面的MapperScanner生成的XxxMapper接口代理是区分数据库的
            sqlSessionFactoryBean.setMapperLocations(properties.resolveMapperLocations());
        }
        Set<String> factoryPropertyNames = Stream
                .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
        Class<? extends LanguageDriver> defaultLanguageDriver = properties.getDefaultScriptingLanguageDriver();
        List<LanguageDriver> languageDrivers = getAgreedNamingBeans(database, LanguageDriver.class);
        if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(languageDrivers)) {
            // Need to mybatis-spring 2.0.2+
            sqlSessionFactoryBean.setScriptingLanguageDrivers(languageDrivers.toArray(new LanguageDriver[0]));
            if (defaultLanguageDriver == null && languageDrivers.size() == 1) {
                defaultLanguageDriver = languageDrivers.get(0).getClass();
            }
        }
        if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
            // Need to mybatis-spring 2.0.2+
            sqlSessionFactoryBean.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
        }
        return sqlSessionFactoryBean;
    }

    protected void applyConfiguration(SqlSessionFactoryBean sqlSessionFactoryBean, String database, MybatisProperties properties) {
        Resource configLocationResource = getConfigLocation(properties.getConfigLocation());
        sqlSessionFactoryBean.setConfigLocation(configLocationResource);
        Configuration configuration = properties.getConfiguration();
        if (configuration == null && configLocationResource == null) {
            configuration = new Configuration();
            configuration.setMapUnderscoreToCamelCase(true); //增加默认设置
        }
        List<ConfigurationCustomizer> configurationCustomizers = getAgreedNamingBeans(database, ConfigurationCustomizer.class);
        if (configuration != null && !CollectionUtils.isEmpty(configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        sqlSessionFactoryBean.setConfiguration(configuration);
    }

}
