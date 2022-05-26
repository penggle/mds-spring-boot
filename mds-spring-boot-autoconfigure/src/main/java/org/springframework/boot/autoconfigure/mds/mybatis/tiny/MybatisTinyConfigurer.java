package org.springframework.boot.autoconfigure.mds.mybatis.tiny;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.mds.MdsComponentsRegistrationLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author pengpeng
 * @since 2.1
 */
public class MybatisTinyConfigurer implements MdsComponentsRegistrationLifecycle, BeanFactoryAware, PriorityOrdered {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void beforeMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {
        /*
         * 由于mybatis-springboot-msd框架初始化多个数据源的数据访问层组件时机太早了，导致MybatisTiny的MybatisBeanPostProcessor处理不了SqlSessionFactory
         * 此处需要将该MybatisBeanPostProcessor提前加入BeanFactory中
         */
        beanFactory.addBeanPostProcessor((BeanPostProcessor) beanFactory.getBean("mybatisBeanPostProcessor"));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
