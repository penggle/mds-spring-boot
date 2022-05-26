package org.springframework.boot.autoconfigure.mds.mybatis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mds.mybatis.plus.MybatisPlusMdsMybatisComponentsBuilder;
import org.springframework.boot.autoconfigure.mds.mybatis.tiny.MybatisTinyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 针对Mybatis扩展框架(诸如MybatisTiny、MybatisPlus)的额外配置
 *
 * @author pengpeng
 * @since 2.1
 */
@Configuration
public class MybatisExtensionConfiguration {

    /**
     * 构建SqlSessionFactoryBean和ClassPathMapperScanner两个bean的Builder
     * 此处为MybatisPlus的实现：MybatisPlusMdsMybatisComponentsBuilder
     */
    @Bean
    @ConditionalOnClass(name="com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean")
    public MdsMybatisComponentsBuilder mybatisPlusMdsMybatisComponentsBuilder() {
        return new MybatisPlusMdsMybatisComponentsBuilder();
    }

    /**
     * 如果当前存在MybatisTiny框架则需要做一些特殊处理以激活MybatisTiny
     * @return
     */
    @Bean
    @ConditionalOnBean(name="mybatisBeanPostProcessor")
    public MybatisTinyConfigurer mybatisTinyConfigurer() {
        return new MybatisTinyConfigurer();
    }

}
