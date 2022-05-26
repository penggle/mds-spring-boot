package org.springframework.boot.autoconfigure.mds.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.mds.*;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * MDS组件之Mybatis部分的Builder接口
 *
 * @author pengpeng
 * @since 2.1
 */
public abstract class MdsMybatisComponentsBuilder extends MdsComponentsBuilderSupport {

    /**
     * 创建SqlSessionFactoryBean
     *
     * @param database          - 数据库名称
     * @param dataSource        - 数据源实例
     * @param properties        - MybatisProperties实例
     * @return
     */
    public abstract FactoryBean<SqlSessionFactory> buildSqlSessionFactoryBean(String database, DataSource dataSource, MybatisProperties properties);

    /**
     * 创建ClassPathMapperScanner
     * 注意：此处不需要调用ClassPathMapperScanner#registerFilters()和scan()两个方法，build逻辑只需要设置相关属性即可
     *
     * @param database
     * @param registry
     * @return
     */
    public ClassPathMapperScanner buildClassPathMapperScanner(String database, BeanDefinitionRegistry registry) {
        String sqlSessionFactoryBeanName = MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanName(database);
        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        classPathMapperScanner.setAddToConfig(true);
        classPathMapperScanner.setAnnotationClass(NamedDatabase.class);
        classPathMapperScanner.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
        classPathMapperScanner.setResourceLoader(getResourceLoader());
        classPathMapperScanner.setEnvironment(getEnvironment());
        classPathMapperScanner.setBeanNameGenerator(new DatabasedBeanNameGenerator(database));
        //通过XxxMapper接口上标记的@NamedDatabase("xxx")来仅为该库下的Mapper生成Mapper实现,实现了区分数据库的目的
        classPathMapperScanner.addExcludeFilter(new DatabasedMapperExcludeFilter(database));
        return classPathMapperScanner;
    }

    /**
     * 获取Mybatis-Mapper接口扫描的basePackage
     *
     * @return
     */
    protected String[] getMapperScanBasePackages() {
        /*
         * 这里需要注意，如果你的SpringBoot启动类所在的包不是basePackage，那么需要在在启动类上指明basePackage，像这样：
         *
         * package com.myapp.boot;
         *
         * @AutoConfigurationPackage(basePackages="com.myapp")
         * @SpringBootApplication(scanBasePackages="com.myapp")
         * public class MyExampleApplication {
         *
         *     public static void main(String[] args) {
         *         SpringApplication.run(MybatisTinyExampleApplication.class, args);
         *      }
         *
         *  }
         *
         */
        List<String> basePackages = AutoConfigurationPackages.get(getBeanFactory());
        return basePackages.toArray(new String[0]);
    }

    /**
     * 解析mybatis-config.xml配置文件
     * @param configLocation
     * @return
     */
    protected Resource getConfigLocation(String configLocation) {
        if(StringUtils.hasText(configLocation)) {
            Resource resource = getResourceLoader().getResource(configLocation);
            return resource.exists() ? resource : null;
        }
        return null;
    }

}
