package org.springframework.boot.autoconfigure.mds.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.mds.*;
import org.springframework.boot.autoconfigure.mds.jdbc.MdsJdbcComponentType;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于mybatis-springboot支持多数据源的数据库访问层(DAL)组件自动注册器
 * 具体通过@NamedDatabase("dbName")来区分数据源(database="dbName")
 * 涉及自动注册的组件有：
 *      4、SqlSessionFactory             beanName=defaultSqlSessionFactory
 *      5、SqlSessionTemplate            beanName=defaultSqlSessionTemplate
 *      6、XxxMapper                     beanName=defaultXxxMapper
 *
 *      以上组件都会区分数据库，生成的beanName都是以{database} + 组件类.class.getSimpleName()这种命名方式
 *
 * @author pengpeng
 * @since 2.1
 */
public class MdsMybatisComponentsRegistry extends AbstractMdsComponentsRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsMybatisComponentsRegistry.class);

    protected final MdsMybatisComponentsBuilder mdsMybatisComponentsBuilder;

    public MdsMybatisComponentsRegistry(MdsMybatisComponentsBuilder mdsMybatisComponentsBuilder) {
        this.mdsMybatisComponentsBuilder = mdsMybatisComponentsBuilder;
    }

    @Override
    protected Set<MdsComponentBean> registerComponentBeans(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        if(hasRequiredDataSource(database)) { //存在指定名称的数据源?
            LOGGER.debug("Prepare to dynamic register Multi-DataSource Mybatis components for Database({}).", database);
            componentBeans.addAll(registerSqlSessionFactoryBean(registry, database)); //4、注册SqlSessionFactoryBean
            componentBeans.addAll(registerSqlSessionTemplateBean(registry, database)); //5、注册SqlSessionTemplate
            componentBeans.addAll(registerMapperByMapperScanner(registry, database)); //6、注册Mapper代理bean
        }
        return componentBeans;
    }

    @Override
    protected void afterComponentsRegisterCompletion(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        getMdsComponentsRegistrationLifecycles().forEach(lifecycle -> lifecycle.onMdsMybatisComponentsRegistered(mdsAnnotationMetadata, registry, mdsComponentBeans.clone()));
    }

    /**
     * 注册SqlSessionFactory
     *
     * @param registry
     * @param database
     */
    protected Set<MdsComponentBean> registerSqlSessionFactoryBean(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String sqlSessionFactoryBeanName = MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanName(database);
        boolean componentExist = beanFactory.containsBean(sqlSessionFactoryBeanName);
        if(!componentExist) {
            BeanDefinition mybatisPropertiesDefinition = createMybatisPropertiesBeanDefinition(database);
            String mybatisPropertiesBeanName = MdsMybatisComponentType.MYBATIS_PROPERTIES.getComponentBeanName(database);
            registry.registerBeanDefinition(mybatisPropertiesBeanName, mybatisPropertiesDefinition); //注册Mybatis配置bean
            MybatisProperties mybatisProperties = beanFactory.getBean(mybatisPropertiesBeanName, MdsMybatisComponentType.MYBATIS_PROPERTIES.getComponentBeanType());
            componentBeans.add(new MdsComponentBean(database, MdsMybatisComponentType.MYBATIS_PROPERTIES, mybatisPropertiesBeanName, mybatisProperties, true));
            LOGGER.debug("Dynamic register MybatisProperties[database = {}] bean ({}) successfully: {}", database, mybatisPropertiesBeanName, mybatisProperties);

            String dataSourceBeanName = MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database);
            DataSource dataSource = beanFactory.getBean(dataSourceBeanName, MdsJdbcComponentType.DATASOURCE.getComponentBeanType());
            FactoryBean<SqlSessionFactory> sqlSessionFactoryBean = mdsMybatisComponentsBuilder.buildSqlSessionFactoryBean(database, dataSource, mybatisProperties);
            BeanDefinition sqlSessionFactoryBeanDefinition = createBeanDefinition(sqlSessionFactoryBean, null);
            registry.registerBeanDefinition(sqlSessionFactoryBeanName, sqlSessionFactoryBeanDefinition); //注册SqlSessionFactory bean
            SqlSessionFactory sqlSessionFactory = beanFactory.getBean(sqlSessionFactoryBeanName, MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanType());
            LOGGER.debug("Dynamic register SqlSessionFactory[database = {}] bean ({}) successfully: {}", database, sqlSessionFactoryBeanName, sqlSessionFactory);
        }
        componentBeans.add(new MdsComponentBean(database, MdsMybatisComponentType.SQL_SESSION_FACTORY, sqlSessionFactoryBeanName, beanFactory.getBean(sqlSessionFactoryBeanName, MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanType()), !componentExist));
        return componentBeans;
    }

    /**
     * 创建MybatisProperties的BeanDefinition
     *
     * @param database
     * @return
     */
    protected BeanDefinition createMybatisPropertiesBeanDefinition(String database) {
        //首先尝试获取区分数据库的MybatisProperties
        MybatisProperties mybatisProperties = MdsMybatisComponentConfig.MYBATIS.getBoundedConfigProperties(environment, database);
        if(mybatisProperties == null) {
            //其次尝试获取不区分数据库的MybatisProperties
            mybatisProperties = MdsMybatisComponentConfig.MYBATIS.getBoundedConfigProperties(environment, null);
        }
        mybatisProperties = mybatisProperties != null ? mybatisProperties : MdsMybatisComponentConfig.MYBATIS.newDefaultConfigProperties();
        return createBeanDefinition(mybatisProperties, MybatisProperties.class);
    }

    /**
     * 注册SqlSessionTemplate
     *
     * @param registry
     * @param database
     */
    protected Set<MdsComponentBean> registerSqlSessionTemplateBean(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String sqlSessionTemplateBeanName = MdsMybatisComponentType.SQL_SESSION_TEMPLATE.getComponentBeanName(database);
        boolean componentExist = beanFactory.containsBean(sqlSessionTemplateBeanName);
        if(!componentExist) {
            String sqlSessionFactoryBeanName = MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanName(database);
            SqlSessionFactory sqlSessionFactory = beanFactory.getBean(sqlSessionFactoryBeanName, MdsMybatisComponentType.SQL_SESSION_FACTORY.getComponentBeanType());
            String mybatisPropertiesBeanName = MdsMybatisComponentType.MYBATIS_PROPERTIES.getComponentBeanName(database);
            MybatisProperties mybatisProperties = beanFactory.getBean(mybatisPropertiesBeanName, MdsMybatisComponentType.MYBATIS_PROPERTIES.getComponentBeanType());
            ExecutorType executorType = mybatisProperties.getExecutorType();
            SqlSessionTemplate sqlSessionTemplate = executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
            BeanDefinition sqlSessionTemplateBeanDefinition = createBeanDefinition(sqlSessionTemplate, MdsMybatisComponentType.SQL_SESSION_TEMPLATE.getComponentBeanType());
            registry.registerBeanDefinition(sqlSessionTemplateBeanName, sqlSessionTemplateBeanDefinition); //注册SqlSessionTemplate bean
            LOGGER.debug("Dynamic register SqlSessionTemplate[database = {}] bean ({}) successfully: {}", database, sqlSessionTemplateBeanName, sqlSessionTemplate);
        }
        componentBeans.add(new MdsComponentBean(database, MdsMybatisComponentType.SQL_SESSION_TEMPLATE, sqlSessionTemplateBeanName, beanFactory.getBean(sqlSessionTemplateBeanName, MdsMybatisComponentType.SQL_SESSION_TEMPLATE.getComponentBeanType()), !componentExist));
        return componentBeans;
    }

    /**
     * 注册Mapper代理bean
     *
     * @param registry
     * @param database
     */
    protected Set<MdsComponentBean> registerMapperByMapperScanner(BeanDefinitionRegistry registry, String database) {
        ClassPathMapperScanner classPathMapperScanner = mdsMybatisComponentsBuilder.buildClassPathMapperScanner(database, registry);
        classPathMapperScanner.registerFilters(); //注册Bean注册过滤器
        Set<MdsComponentBean> componentBeans = scanMappers(registry, database, classPathMapperScanner);
        LOGGER.debug("Dynamic register XxxMapper[database = {}] beans(Total {}) successfully", database, componentBeans.size());
        return componentBeans;
    }

    /**
     * 扫描并注册Mybatis-Mapper代理
     *
     * @param registry
     * @param database
     * @param classPathMapperScanner
     * @return
     */
    protected Set<MdsComponentBean> scanMappers(BeanDefinitionRegistry registry, String database, ClassPathMapperScanner classPathMapperScanner) {
        Set<BeanDefinitionHolder> mapperBeanDefinitions = classPathMapperScanner.doScan(mdsMybatisComponentsBuilder.getMapperScanBasePackages());
        AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
        return mapperBeanDefinitions.stream().map(beanDefinition -> {
            String mapperBeanName = beanDefinition.getBeanName();
            return new MdsComponentBean(database, MdsMybatisComponentType.MYBATIS_MAPPER, mapperBeanName, beanFactory.getBean(mapperBeanName), true);
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public final int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

}
