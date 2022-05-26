package org.springframework.boot.autoconfigure.mds;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

/**
 * MDS组件注册Lifecycle，
 * 实现该接口可以在MDS组件动态注册的前后做一些事情，如果你有这个需求的话。
 * 为什么这样实现，而不采用Spring自带的事件机制？主要是因为Spring事件是异步的且存在迟延发送的问题，某些场景下满足不了定制化的需求了
 * 该Listener的方法调用顺序严格按照如下顺序：
 *  
 *  1、{@link #beforeMdsComponentsRegistered(AnnotationMetadata, BeanDefinitionRegistry)}
 *  2、{@link #onMdsJdbcComponentsRegistered(AnnotationMetadata, BeanDefinitionRegistry, MdsComponentBeans)}
 *  3、{@link #onMdsMybatisComponentsRegistered(AnnotationMetadata, BeanDefinitionRegistry, MdsComponentBeans)}
 *  4、{@link #afterMdsComponentsRegistered(AnnotationMetadata, BeanDefinitionRegistry, MdsComponentBeans)}
 *
 * @author pengpeng
 * @since 2.1
 */
public interface MdsComponentsRegistrationLifecycle {

    /**
     * 在动态注册MDS组件之前做一些事件
     *
     * @param mdsAnnotationMetadata     - 可以取到{@link EnableMultiDataSource}注解的元数据信息
     * @param registry                  - Spring Bean注册器
     */
    default void beforeMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {

    }

    /**
     * 在MDS之JDBC组件(DataSource、JdbcTemplate、TransactionManager)动态注册完毕之后做一些事情
     *
     * @param mdsAnnotationMetadata
     * @param registry
     * @param mdsComponentBeans
     */
    default void onMdsJdbcComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {

    }

    /**
     * 在MDS之Mybatis组件(SqlSessionFactory、SqlSessionTemplate、XxxMapper)动态注册完毕之后做一些事情
     *
     * @param mdsAnnotationMetadata
     * @param registry
     * @param mdsComponentBeans
     */
    default void onMdsMybatisComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {

    }

    /**
     * 在所有MDS组件动态注册完毕之后做一些事情
     *
     * @param mdsAnnotationMetadata     - 可以取到{@link EnableMultiDataSource}注解的元数据信息
     * @param registry                  - Spring Bean注册器
     * @param mdsComponentBeans         - 已动态注册的MDS组件
     */
    default void afterMdsComponentsRegistered(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry, MdsComponentBeans mdsComponentBeans) {

    }

}
