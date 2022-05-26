package com.penglecode.codeforce.mybatismds.examples.common.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengpeng
 * @since 2.1
 */
@Configuration
public class MyExampleConfiguration implements InitializingBean, BeanFactoryAware {

    private BeanFactory beanFactory;

    /*@Bean
    @ConfigurationProperties(prefix="myexample")
    public MyExampleProperties myExampleProperties() {
        return new MyExampleProperties();
    }*/

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("==> beanFactory = " + beanFactory);
    }
}
