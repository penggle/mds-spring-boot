package com.penglecode.codeforce.mybatismds.examples.common.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.mds.MdsComponentBeans;
import org.springframework.boot.autoconfigure.mds.MdsComponentsRegistrationLifecycle;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 自定义的MDS组件注册Lifecycle，
 *
 * @author pengpeng
 * @since 2.1
 */
@Component
public class CustomMdsComponentsRegistrationLifecycle implements MdsComponentsRegistrationLifecycle {

    @Override
    public void beforeMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {
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
        System.out.println(">>> Multi-DataSource Components Registration Completed => " + mdsComponentBeans);
    }

}
