package com.penglecode.codeforce.mybatismds.examples.common.config;

import org.apache.shardingsphere.sharding.spring.boot.ShardingRuleSpringBootConfiguration;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.apache.shardingsphere.spring.boot.registry.AbstractAlgorithmProvidedBeanRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mds.MdsComponentBeans;
import org.springframework.boot.autoconfigure.mds.MdsComponentsRegistrationLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 手动配置ShardingSphere的配置类
 * mybatis-springboot-msd框架默认排除了ShardingSphere的自动配置类，改在这里手动@Import导入，
 * 主要是因为mybatis-springboot-msd框架初始化多个数据源的数据访问层组件时机太早了，此时{@link ShardingSphereAutoConfiguration}和{@link ShardingRuleSpringBootConfiguration}
 * 两个配置类还没有注册到Spring上下文中，更别谈加载其自动配置。在此处手动@Import导入是想借助Spring对{@link MdsComponentsRegistrationLifecycle}的加载顺带勾起
 * 对他们两个的加载。但是这里面也有一些瑕疵需要使用{@link #prepareShardingSphereComponents(BeanDefinitionRegistry)}和{@link #destroyShardingSphereAlgorithmRegistries(BeanDefinitionRegistry)}两个方法来弥补
 *
 * @author pengpeng
 * @since 2.1
 */
@Configuration
@Import({ShardingSphereAutoConfiguration.class, ShardingRuleSpringBootConfiguration.class})
@ConditionalOnProperty(prefix="spring.shardingsphere", name="enabled", havingValue="true", matchIfMissing=true)
public class ShardingSphereConfiguration implements MdsComponentsRegistrationLifecycle, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void beforeMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {
        prepareShardingSphereComponents(registry);
        System.out.println(">>> Multi-DataSource Components Registration Starting ...");
    }

    @Override
    public void onMdsJdbcComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        System.out.println(">>> Multi-DataSource Jdbc Components Registered => " + mdsComponentBeans);
    }

    @Override
    public void onMdsMybatisComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        System.out.println(">>> Multi-DataSource Mybatis Components Registered => " + mdsComponentBeans);
    }

    @Override
    public void afterMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {
        destroyShardingSphereAlgorithmRegistries(registry);
        System.out.println(">>> Multi-DataSource Components Registration Completed => " + mdsComponentBeans);
    }

    /**
     * 由于mybatis-springboot-msd框架初始化多个数据源的数据访问层组件时机太早了，
     * 此处需要提前准备ShardingSphere相关的组件配置
     *
     * @param registry
     */
    @SuppressWarnings("rawtypes")
    protected void prepareShardingSphereComponents(BeanDefinitionRegistry registry) {
        /*
         * 由于加载ShardingSphere的自动配置类过早，导致自动配置类无法实例化，此处通过变现修改AbstractBeanDefinition#autowireMode为AUTOWIRE_CONSTRUCTOR
         * 来规避Configuration类实例化报错的问题
         */
        AbstractBeanDefinition beanDefinition1 = (AbstractBeanDefinition) registry.getBeanDefinition(ShardingSphereAutoConfiguration.class.getName());
        beanDefinition1.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        AbstractBeanDefinition beanDefinition2 = (AbstractBeanDefinition) registry.getBeanDefinition(ShardingRuleSpringBootConfiguration.class.getName());
        beanDefinition2.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        /*
         * 提前注册ShardingSphere的算法
         * 由于加载ShardingSphere的自动配置类过早，AbstractAlgorithmProvidedBeanRegistry的实现(例如ShardingAlgorithmProvidedBeanRegistry、KeyGenerateAlgorithmProvidedBeanRegistry等)
         * 动态注册ShardingSphereAlgorithm的时机较晚，所以此处手动促其提前注册
         */
        List<AbstractAlgorithmProvidedBeanRegistry> algorithmBeanRegistries = beanFactory.getBeanProvider(AbstractAlgorithmProvidedBeanRegistry.class).orderedStream().collect(Collectors.toList());
        algorithmBeanRegistries.forEach(processor -> processor.postProcessBeanDefinitionRegistry(registry));
        List<BeanPostProcessor> beanPostProcessors = beanFactory.getBeanPostProcessors();
        beanPostProcessors.addAll(algorithmBeanRegistries);
        /*
         * 订单库(order)是基于ShardingSphere分库分表的，根据mybatis-springboot-mds约定：
         * 订单库(order)作为外部数据源，那么Spring-BeanFactory中必须有一个name=orderDataSource的数据源(DataSource)
         * 所以此处给默认的shardingSphereDataSource起个别名
         */
        registry.registerAlias("shardingSphereDataSource", "orderDataSource");
    }

    /**
     * 由于在前面(prepareShardingSphereComponents()方法中)通过非常规手段提前注册了ShardingSphere的算法，
     * 在注册完成之后，需要将算法注册器AbstractAlgorithmProvidedBeanRegistry给销毁掉，否则后面报Bean重新定义冲突异常
     * 冲突是由于{@link org.springframework.context.support.PostProcessorRegistrationDelegate}在后面又调用了
     * BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry()方法导致重复注册引起的
     *
     * @param registry
     */
    @SuppressWarnings("rawtypes")
    protected void destroyShardingSphereAlgorithmRegistries(BeanDefinitionRegistry registry) {
        Map<String,AbstractAlgorithmProvidedBeanRegistry> algorithmBeanRegistries = beanFactory.getBeansOfType(AbstractAlgorithmProvidedBeanRegistry.class);
        List<BeanPostProcessor> beanPostProcessors = beanFactory.getBeanPostProcessors();
        for(Map.Entry<String,AbstractAlgorithmProvidedBeanRegistry> entry : algorithmBeanRegistries.entrySet()) {
            String beanName = entry.getKey();
            AbstractAlgorithmProvidedBeanRegistry algorithmBeanRegistry = entry.getValue();
            beanPostProcessors.remove(algorithmBeanRegistry);
            beanFactory.destroyBean(beanName, algorithmBeanRegistry);
            registry.removeBeanDefinition(beanName);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

}
