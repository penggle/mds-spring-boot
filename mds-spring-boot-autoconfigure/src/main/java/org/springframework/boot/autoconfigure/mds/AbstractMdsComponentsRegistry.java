package org.springframework.boot.autoconfigure.mds;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.mds.jdbc.MdsJdbcComponentType;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于mybatis-springboot支持多数据源的数据库访问层组件自动注册器
 * 具体通过@NamedDatabase("dbName")来区分数据源(database="dbName")
 * 涉及自动注册的组件有：
 *      1、DataSource
 *      2、JdbcTemplate
 *      3、TransactionManager
 *      4、SqlSessionFactory
 *      5、SqlSessionTemplate
 *      6、XxxMapper
 *
 *      以上组件都会区分数据库，生成的beanName都是以{database} + 组件类.getClass().getSimpleName()这种命名方式
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMdsComponentsRegistry implements BeanFactoryAware, EnvironmentAware, ResourceLoaderAware, Ordered {

    protected ListableBeanFactory beanFactory;

    protected ConfigurableEnvironment environment;

    protected ResourceLoader resourceLoader;

    public void registerBeanDefinitions(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        AnnotationAttributes componentAttributes = AnnotationAttributes.fromMap(mdsAnnotationMetadata.getAnnotationAttributes(EnableMultiDataSource.class.getName()));
        if(componentAttributes != null) {
            AnnotationAttributes[] databaseAttributesArray = componentAttributes.getAnnotationArray("value");
            if(databaseAttributesArray.length > 0) {
                for(AnnotationAttributes databaseAttributes : databaseAttributesArray) {
                    String database = databaseAttributes.getString("value");
                    Set<MdsComponentBean> databasedComponentBeans = registerComponentBeans(registry, database);

                    mdsComponentBeans.merge(database, databasedComponentBeans, (olds, news) -> {
                        olds.addAll(news);
                        return olds;
                    });
                }
                postComponentsRegistered(registry, mdsComponentBeans);
                afterComponentsRegisterCompletion(mdsAnnotationMetadata, registry, mdsComponentBeans);
            }
        }
    }

    /**
     * 注册指定database的MDS组件
     *
     * @param registry
     * @param database
     */
    protected abstract Set<MdsComponentBean> registerComponentBeans(BeanDefinitionRegistry registry, String database);

    /**
     * 在JDBC组件或Mybatis组件注册完成后做一些事情
     *
     * @param registry
     * @param mdsComponentBeans
     */
    protected void postComponentsRegistered(BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {

    }

    /**
     * 当JDBC组件或Mybatis组件注册全部完成之后做一些事情
     *
     * @param mdsAnnotationMetadata
     * @param registry
     * @param mdsComponentBeans
     */
    protected abstract void afterComponentsRegisterCompletion(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans);

    /**
     * Spring上下文容器中是否存在对应database的DataSource?
     *
     * @param database  - 数据库名称
     * @return
     */
    protected boolean hasRequiredDataSource(String database) {
        return beanFactory.containsBean(MdsJdbcComponentType.DATASOURCE.getComponentBeanName(database));
    }

    /**
     * 将Environment中指定bindName开头的配置装配到指定的bean类型上
     *
     * @param beanClass		- 需要动态注册的bean的类型
     * @param bindName		- 相当于@ConfigurationProperties注解的prefix字段
     * @param <T>
     * @return
     */
    protected <T> BeanDefinition createBindableBeanDefinition(Class<T> beanClass, String bindName) {
        Assert.notNull(beanClass, "Parameter 'beanClass' must be required!");
        Assert.hasText(bindName, "Parameter 'bindName' must be required!");
        T bindObject = Binder.get(environment).bind(bindName, beanClass).orElseThrow(IllegalStateException::new);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass, () -> bindObject);
        return beanDefinitionBuilder.getBeanDefinition();
    }

    /**
     * 将Environment中指定bindName开头的配置装配到指定的bean实例上
     *
     * @param beanInstance	- 需要动态注册的bean的给定示例
     * @param beanClass		- 需要动态注册的bean的类型
     * @param bindName		- 相当于@ConfigurationProperties注解的prefix字段
     * @param <T>
     * @return
     */
    protected <T> BeanDefinition createBindableBeanDefinition(T beanInstance, Class<T> beanClass, String bindName) {
        Assert.notNull(beanInstance, "Parameter 'beanInstance' must be required!");
        Assert.hasText(bindName, "Parameter 'bindName' must be required!");
        T bindObject = Binder.get(environment).bind(bindName, Bindable.ofInstance(beanInstance)).orElse(beanInstance);
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass != null ? beanClass : (Class<T>)beanInstance.getClass(), () -> bindObject);
        return beanDefinitionBuilder.getBeanDefinition();
    }

    /**
     * 创建给定实例的BeanDefinition
     *
     * @param beanInstance	- 需要动态注册的bean的给定示例
     * @param beanClass		- 需要动态注册的bean的类型
     * @param <T>
     * @return
     */
    protected <T> BeanDefinition createBeanDefinition(T beanInstance, Class<T> beanClass) {
        Assert.notNull(beanInstance, "Parameter 'beanInstance' must be required!");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass != null ? beanClass : (Class<T>)beanInstance.getClass(), () -> beanInstance);
        return beanDefinitionBuilder.getBeanDefinition();
    }

    protected List<MdsComponentsRegistrationLifecycle> getMdsComponentsRegistrationLifecycles() {
        return beanFactory.getBeanProvider(MdsComponentsRegistrationLifecycle.class).orderedStream().collect(Collectors.toList());
    }

    protected Set<MdsComponentBean> newComponentBeans() {
        return new LinkedHashSet<>();
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public final void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
