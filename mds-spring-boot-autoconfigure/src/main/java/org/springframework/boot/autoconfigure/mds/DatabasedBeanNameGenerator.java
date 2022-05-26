package org.springframework.boot.autoconfigure.mds;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.StringUtils;


/**
 * 基于@NamedDatabase("dbName")注解，区分数据库的Mapper beanName生成器实现
 *
 * @author pengpeng
 * @since 2.1
 */
public class DatabasedBeanNameGenerator implements BeanNameGenerator {

    private final String targetDatabase;

    public DatabasedBeanNameGenerator(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) definition;
        String beanClassName = genericBeanDefinition.getBeanClassName();
        if(!StringUtils.hasText(beanClassName)) {
            beanClassName = genericBeanDefinition.getBeanClass().getName();
        }
        return MdsComponentType.getComponentBeanName(targetDatabase, beanClassName);
    }

}