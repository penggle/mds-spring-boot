package org.springframework.boot.autoconfigure.mds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于mybatis-springboot支持多数据源的数据库访问层自动配置类
 *
 * @author pengpeng
 * @since 2.1
 */
class MdsComponentRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsComponentRegistrar.class);

    private ListableBeanFactory beanFactory;

    /**
     * 注册MDS组件
     *
     * @param mdsAnnotationMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata mdsAnnotationMetadata, BeanDefinitionRegistry registry) {
        List<AbstractMdsComponentsRegistry> dsComponentsRegistries = beanFactory.getBeanProvider(AbstractMdsComponentsRegistry.class).orderedStream().collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(dsComponentsRegistries)) {
            List<MdsComponentsRegistrationLifecycle> mdsComponentsRegistrationLifecycles = getMdsComponentsRegistrationLifecycles();
            mdsComponentsRegistrationLifecycles.forEach(listener -> listener.beforeMdsComponentsRegistered(mdsAnnotationMetadata, registry));
            MdsComponentBeans mdsComponentBeans = new MdsComponentBeans();
            for(AbstractMdsComponentsRegistry mdsComponentsRegistry : dsComponentsRegistries) {
                mdsComponentsRegistry.registerBeanDefinitions(mdsAnnotationMetadata, registry, mdsComponentBeans);
            }
            if(mdsComponentBeans.getDefaultTransactionManager() != null) {
                LOGGER.info("Dynamic register all multi-datasource components successfully : {}", mdsComponentBeans);
            }
            mdsComponentsRegistrationLifecycles.forEach(listener -> listener.afterMdsComponentsRegistered(mdsAnnotationMetadata, registry, mdsComponentBeans.clone()));
        }
    }

    protected List<MdsComponentsRegistrationLifecycle> getMdsComponentsRegistrationLifecycles() {
        return beanFactory.getBeanProvider(MdsComponentsRegistrationLifecycle.class).orderedStream().collect(Collectors.toList());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

}
