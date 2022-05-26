package org.springframework.boot.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 提前于{@link ConfigurationClassPostProcessor}之前注册SpringBoot的基础设施，例如激活{@link EnableConfigurationProperties}所必须的基础设施
 *
 * 当前该{@link BeanDefinitionRegistryPostProcessor}实现了PriorityOrdered，那么决定了它与{@link ConfigurationClassPostProcessor}是一起执行的
 * 同时其实现了getOrder(),提高了执行优先级，决定了当前实现比{@link ConfigurationClassPostProcessor}早一步执行
 *
 * @see org.springframework.context.support.PostProcessorRegistrationDelegate
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public class SpringBootInfrastructurePreInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, BeanDefinitionRegistryPostProcessor, PriorityOrdered {

    public static final String BEAN_NAME = SpringBootInfrastructurePreInitializer.class.getName();

    private ConfigurableApplicationContext applicationContext;

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        preInitialize(applicationContext);
        registerInfrastructures();
        registerMySelf();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        initInfrastructures();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //too late, nothing to do
    }

    protected void preInitialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
        //一般情况下默认的BeanFactory(ROOT)就是BeanDefinitionRegistry
        if(beanFactory instanceof BeanDefinitionRegistry) {
            this.beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        } else {
            this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
        }
    }

    /**
     * 提前注册SpringBoot的相关基础设施组件
     */
    protected void registerInfrastructures() {
        //激活@EnableConfigurationProperties注解
        String registrarClassName = "org.springframework.boot.context.properties.EnableConfigurationPropertiesRegistrar";
        Class<? extends ImportBeanDefinitionRegistrar> registrarClass = (Class<? extends ImportBeanDefinitionRegistrar>) ClassUtils.resolveClassName(registrarClassName, ClassUtils.getDefaultClassLoader());
        Method staticMethod;

        staticMethod = ReflectionUtils.findMethod(registrarClass, "registerInfrastructureBeans", BeanDefinitionRegistry.class);
        Assert.notNull(staticMethod, String.format("No method named '%s' found in %s!", "registerInfrastructureBeans", registrarClassName));
        staticMethod.setAccessible(true);
        ReflectionUtils.invokeMethod(staticMethod, null, beanDefinitionRegistry);

        staticMethod = ReflectionUtils.findMethod(registrarClass, "registerMethodValidationExcludeFilter", BeanDefinitionRegistry.class);
        Assert.notNull(staticMethod, String.format("No method named '%s' found in %s!", "registerMethodValidationExcludeFilter", registrarClassName));
        staticMethod.setAccessible(true);
        ReflectionUtils.invokeMethod(staticMethod, null, beanDefinitionRegistry);
    }

    /**
     * 注册当前这个Initializer类到Spring上下文中去，这样其实现的方法{@link #postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}
     * 配合{@link PriorityOrdered}才能提前于{@link ConfigurationClassPostProcessor}执行
     * 这个逻辑见PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(..)
     * @see org.springframework.context.support.PostProcessorRegistrationDelegate
     */
    protected void registerMySelf() {
        BeanDefinition thisBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SpringBootInfrastructurePreInitializer.class, () -> this).getBeanDefinition();
        this.beanDefinitionRegistry.registerBeanDefinition(BEAN_NAME, thisBeanDefinition);
    }

    /**
     * 初始化SpringBoot的相关基础设施
     */
    protected void initInfrastructures() {
        //提前将ConfigurationPropertiesBindingPostProcessor加入到BeanFactory中去
        beanFactory.addBeanPostProcessor(beanFactory.getBean(ConfigurationPropertiesBindingPostProcessor.BEAN_NAME, ConfigurationPropertiesBindingPostProcessor.class));
    }


    /**
     * 只要比{@link ConfigurationClassPostProcessor}优先级高就行了
     * @return
     */
    @Override
    public final int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    protected ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return beanDefinitionRegistry;
    }

    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

}
