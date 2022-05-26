package org.springframework.boot.autoconfigure.mds.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.mds.*;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableMultiTransactionManagement;
import org.springframework.transaction.interceptor.MultiTransactionInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于mybatis-springboot支持多数据源的数据库访问层(DAL)组件自动注册器
 * 具体通过@NamedDatabase("dbName")来区分数据源(database="dbName")
 * 涉及自动注册的组件有：
 *      1、DataSource                beanName=defaultDataSource
 *      2、JdbcTemplate              beanName=defaultJdbcTemplate
 *      3、TransactionManager        beanName=defaultTransactionManager
 *
 *      以上组件都会区分数据库，生成的beanName都是以{database} + 组件类.class.getSimpleName()这种命名方式
 *
 * @author pengpeng
 * @since 2.1
 */
public class MdsJdbcComponentsRegistry extends AbstractMdsComponentsRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsJdbcComponentsRegistry.class);

    protected final MdsJdbcComponentsBuilder mdsJdbcComponentsBuilder;

    public MdsJdbcComponentsRegistry(MdsJdbcComponentsBuilder mdsJdbcComponentsBuilder) {
        this.mdsJdbcComponentsBuilder = mdsJdbcComponentsBuilder;
    }

    @Override
    protected Set<MdsComponentBean> registerComponentBeans(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String dataSourceBeanName = MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database);
        DataSourceProperties dataSourceProperties = MdsJdbcComponentConfig.DATASOURCE.getBoundedConfigProperties(environment, database);
        if(beanFactory.containsBean(dataSourceBeanName) //已经存在该数据源了? 考虑诸如shardingsphere-jdbc这种(Client-Proxy模式)情况(需要手动配置相应的数据源bean)
                || dataSourceProperties != null //是否存在指定数据库的数据源必要配置? 考虑常规多数据库情况，或者诸如shardingsphere-proxy、mycat这种(Server-Proxy模式)情况
        ) {
            LOGGER.debug("Prepare to dynamic register Multi-DataSource Jdbc components for Database({}).", database);
            componentBeans.addAll(registerDataSource(registry, database, dataSourceProperties)); //1、注册DataSource
            componentBeans.addAll(registerJdbcTemplate(registry, database)); //2、注册JdbcTemplate
            componentBeans.addAll(registerTransactionManager(registry, database)); //3、注册PlatformTransactionManager
        } else {
            throw new IllegalStateException(String.format("Can not determine the DataSource bean(%s) for @NamedDatabase(\"%s\"), please check your configuration!", dataSourceBeanName, database));
        }
        return componentBeans;
    }

    @Override
    protected void afterComponentsRegisterCompletion(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        getMdsComponentsRegistrationLifecycles().forEach(lifecycle -> lifecycle.onMdsJdbcComponentsRegistered(mdsAnnotationMetadata, registry, mdsComponentBeans.clone()));
    }

    /**
     * 注册DataSource
     *
     * @param registry
     * @param database
     * @param dataSourceProperties
     */
    protected Set<MdsComponentBean> registerDataSource(BeanDefinitionRegistry registry, String database, DataSourceProperties dataSourceProperties) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String dataSourceBeanName = MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database);
        boolean componentExist = beanFactory.containsBean(dataSourceBeanName);
        if(!componentExist) { //没有注册该bean?
            BeanDefinition dataSourcePropertiesDefinition = createBeanDefinition(dataSourceProperties, MdsJdbcComponentType.DATASOURCE_PROPERTIES.getComponentBeanType());
            String dataSourcePropertiesBeanName = MdsJdbcComponentType.DATASOURCE_PROPERTIES.getComponentBeanName(database);
            registry.registerBeanDefinition(dataSourcePropertiesBeanName, dataSourcePropertiesDefinition); //注册数据源配置bean
            dataSourceProperties = beanFactory.getBean(dataSourcePropertiesBeanName, MdsJdbcComponentType.DATASOURCE_PROPERTIES.getComponentBeanType());
            componentBeans.add(new MdsComponentBean(database, MdsJdbcComponentType.DATASOURCE_PROPERTIES, dataSourcePropertiesBeanName, dataSourceProperties, true));
            LOGGER.debug("Dynamic register DataSourceProperties[database = {}] bean ({}) successfully: {}", database, dataSourcePropertiesBeanName, dataSourceProperties);
            //创建DataSource数据源实例(此时未设置任何连接池配置)
            DataSource dataSource = mdsJdbcComponentsBuilder.buildDataSource(database, dataSourceProperties);
            //将当前数据源的公共连接池配置设置进去
            String poolTypeName = mdsJdbcComponentsBuilder.getPoolTypeName(database, dataSourceProperties);
            BeanDefinition dataSourceDefinition;
            if(StringUtils.hasText(poolTypeName)) {
                dataSourceDefinition = createBindableBeanDefinition(dataSource, MdsJdbcComponentType.DATASOURCE.getComponentBeanType(), MdsJdbcComponentConfig.DATASOURCE.getConfigPrefixOf(poolTypeName));
            } else {
                dataSourceDefinition = createBeanDefinition(dataSource, MdsJdbcComponentType.DATASOURCE.getComponentBeanType());
            }
            registry.registerBeanDefinition(dataSourceBeanName, dataSourceDefinition); //注册数据源配bean
            LOGGER.debug("Dynamic register DataSource[database = {}] bean ({}) successfully: {}", database, dataSourceBeanName, dataSource);
        }
        componentBeans.add(new MdsComponentBean(database, MdsJdbcComponentType.DATASOURCE, dataSourceBeanName, beanFactory.getBean(dataSourceBeanName, MdsJdbcComponentType.DATASOURCE.getComponentBeanType()), !componentExist));
        return componentBeans;
    }

    /**
     * 注册JdbcTemplate
     *
     * @param registry
     * @param database
     */
    protected Set<MdsComponentBean> registerJdbcTemplate(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String jdbcTemplateBeanName = MdsJdbcComponentType.JDBC_TEMPLATE.getComponentBeanName(database);
        boolean componentExist = beanFactory.containsBean(jdbcTemplateBeanName);
        if(!componentExist) { //没有注册该bean?
            String dataSourceBeanName = MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database);
            DataSource dataSource = beanFactory.getBean(dataSourceBeanName, MdsJdbcComponentType.DATASOURCE.getComponentBeanType());
            JdbcTemplate jdbcTemplate = createJdbcTemplate(dataSource, database);
            BeanDefinition jdbcTemplateDefinition = createBeanDefinition(jdbcTemplate, MdsJdbcComponentType.JDBC_TEMPLATE.getComponentBeanType());
            registry.registerBeanDefinition(jdbcTemplateBeanName, jdbcTemplateDefinition); //注册JdbcTemplate
            jdbcTemplate = beanFactory.getBean(jdbcTemplateBeanName, MdsJdbcComponentType.JDBC_TEMPLATE.getComponentBeanType());
            LOGGER.debug("Dynamic register JdbcTemplate[database = {}] bean ({}) successfully: {}", database, jdbcTemplateBeanName, jdbcTemplate);
        }
        componentBeans.add(new MdsComponentBean(database, MdsJdbcComponentType.JDBC_TEMPLATE, jdbcTemplateBeanName, beanFactory.getBean(jdbcTemplateBeanName, MdsJdbcComponentType.JDBC_TEMPLATE.getComponentBeanType()), !componentExist));
        return componentBeans;
    }

    /**
     * 创建JdbcTemplate
     *
     * @param dataSource
     * @param database
     * @return
     */
    protected JdbcTemplate createJdbcTemplate(DataSource dataSource, String database) {
        JdbcProperties jdbcProperties = MdsJdbcComponentConfig.JDBC.getBoundedConfigProperties(environment, database);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcProperties.Template template = jdbcProperties.getTemplate();
        jdbcTemplate.setFetchSize(template.getFetchSize());
        jdbcTemplate.setMaxRows(template.getMaxRows());
        if (template.getQueryTimeout() != null) {
            jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
        }
        return jdbcTemplate;
    }

    /**
     * 注册PlatformTransactionManager
     *
     * @param registry
     * @param database
     */
    protected Set<MdsComponentBean> registerTransactionManager(BeanDefinitionRegistry registry, String database) {
        Set<MdsComponentBean> componentBeans = newComponentBeans();
        String transactionManagerBeanName = MdsJdbcComponentType.TRANSACTION_MANAGER.getComponentBeanName(database);
        boolean componentExist = beanFactory.containsBean(transactionManagerBeanName);
        if(!componentExist) { //没有注册该bean?
            String dataSourceBeanName = MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database);
            DataSource dataSource = beanFactory.getBean(dataSourceBeanName, MdsJdbcComponentType.DATASOURCE.getComponentBeanType());
            DataSourceTransactionManager dataSourceTransactionManager = createDataSourceTransactionManager(dataSource, database);
            BeanDefinition transactionManagerDefinition = createBeanDefinition(dataSourceTransactionManager, MdsJdbcComponentType.TRANSACTION_MANAGER.getComponentBeanType());
            registry.registerBeanDefinition(transactionManagerBeanName, transactionManagerDefinition); //注册DataSourceTransactionManager
            LOGGER.debug("Dynamic register DataSourceTransactionManager[database = {}] bean ({}) successfully: {}", database, transactionManagerBeanName, dataSourceTransactionManager);
        }
        componentBeans.add(new MdsComponentBean(database, MdsJdbcComponentType.TRANSACTION_MANAGER, transactionManagerBeanName, beanFactory.getBean(transactionManagerBeanName, MdsJdbcComponentType.TRANSACTION_MANAGER.getComponentBeanType()), !componentExist));
        return componentBeans;
    }

    /**
     * 创建指定数据源的事务管理器
     *
     * @param dataSource
     * @param database
     * @return
     */
    protected DataSourceTransactionManager createDataSourceTransactionManager(DataSource dataSource, String database) {
        return environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE)
                    ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource);
    }

    /**
     * 注册默认的多数据源事务管理器
     *
     * @see MultiTransactionInterceptor#getDefaultTransactionManager()
     * @param registry
     * @param transactionManagerBeans
     */
    protected MdsComponentBean registerDefaultTransactionManager(BeanDefinitionRegistry registry, List<MdsComponentBean> transactionManagerBeans) {
        String defaultTransactionManagerBeanName = EnableMultiTransactionManagement.DEFAULT_TRANSACTION_MANAGER_NAME;
        transactionManagerBeans.sort(Comparator.comparing(MdsComponentBean::getComponentBeanName, String::compareTo));
        if(transactionManagerBeans.size() > 1) { //如果存在多个事务管理器,则默认的事务管理器为ChainedTransactionManager类型
            List<String> databases = transactionManagerBeans.stream().map(MdsComponentBean::getDatabase).collect(Collectors.toList());
            PlatformTransactionManager[] multiTransactionManagers = transactionManagerBeans.stream()
                    .map(component -> (PlatformTransactionManager) component.getComponentBean())
                    .toArray(PlatformTransactionManager[]::new);
            ChainedTransactionManager defaultTransactionManager = new ChainedTransactionManager(multiTransactionManagers);
            BeanDefinition defaultTransactionManagerBeanDefinition = createBeanDefinition(defaultTransactionManager, ChainedTransactionManager.class);
            registry.registerBeanDefinition(defaultTransactionManagerBeanName, defaultTransactionManagerBeanDefinition);
            LOGGER.debug("Dynamic register default TransactionManager {}[databases = {}] bean (allTransactionManager) successfully: {}", defaultTransactionManager.getClass().getSimpleName(), databases, defaultTransactionManager);
        } else { //否则给仅有的那个事务管理器取个Spring Bean的别名
            MdsComponentBean defaultTransactionManagerBean = transactionManagerBeans.get(0);
            registry.registerAlias(defaultTransactionManagerBean.getComponentBeanName(), defaultTransactionManagerBeanName);
            LOGGER.debug("Dynamic register default TransactionManager {}[databases = {}] bean (allTransactionManager) successfully: {}", defaultTransactionManagerBean.getComponentBean().getClass().getSimpleName(), defaultTransactionManagerBean.getDatabase(), defaultTransactionManagerBean.getComponentBean());
        }
        return new MdsComponentBean(MdsJdbcComponentType.TRANSACTION_MANAGER, defaultTransactionManagerBeanName, beanFactory.getBean(defaultTransactionManagerBeanName, PlatformTransactionManager.class), true);
    }

    @Override
    protected void postComponentsRegistered(BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        super.postComponentsRegistered(registry, mdsComponentBeans);
        List<MdsComponentBean> transactionManagerBeans = mdsComponentBeans.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(component -> MdsJdbcComponentType.TRANSACTION_MANAGER.equals(component.getComponentType()))
                .collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(transactionManagerBeans)) {
            mdsComponentBeans.setDefaultTransactionManager(registerDefaultTransactionManager(registry, transactionManagerBeans));
        }
    }

    @Override
    public final int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2000;
    }

}
