package org.springframework.boot.autoconfigure.mds;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.JmxUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MDS组件Builder支撑类
 *
 * @author pengpeng
 * @since 2.1
 */
public abstract class MdsComponentsBuilderSupport implements ApplicationContextAware, EnvironmentAware, ResourceLoaderAware, BeanFactoryAware {

    private ConfigurableApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    private ConfigurableListableBeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    /**
     * 获取约定名称的(即区分数据库的)指定类型Bean(多个)
     * @param database
     * @param beanClass
     * @param <T>
     * @return
     */
    protected <T> List<T> getAgreedNamingBeans(String database, Class<T> beanClass) {
        List<T> candidateBeans = new ArrayList<>();
        Map<String,T> beanMaps = getBeanFactory().getBeansOfType(beanClass);
        for(Map.Entry<String,T> entry : beanMaps.entrySet()) {
            String realBeanName = entry.getKey();
            T beanInstance = entry.getValue();
            String agreedBeanName = MdsComponentType.getComponentBeanName(database, beanInstance.getClass());
            if(agreedBeanName.equals(realBeanName)) {
                candidateBeans.add(beanInstance);
            }
        }
        return candidateBeans;
    }

    /**
     * 获取约定名称的(即区分数据库的)指定类型Bean(单个)
     * @param database
     * @param beanClass
     * @param <T>
     * @return
     */
    protected <T> T getAgreedNamingBean(String database, Class<T> beanClass) {
        String agreedBeanName = MdsComponentType.getComponentBeanName(database, beanClass);
        try {
            return getBeanFactory().getBean(agreedBeanName, beanClass);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 如果是JNDI数据源，避免暴露数据源信息
     * @param candidate
     * @param beanName
     */
    protected void excludeMBeanIfNecessary(Object candidate, String beanName) {
        for (MBeanExporter mbeanExporter : applicationContext.getBeansOfType(MBeanExporter.class).values()) {
            if (JmxUtils.isMBean(candidate.getClass())) {
                mbeanExporter.addExcludedBean(beanName);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public final void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public final void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    protected ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    protected ListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    protected ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

}
