package org.springframework.boot.autoconfigure.mds.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MDS之Mybatis组件自动配置
 *
 * @author pengpeng
 * @since 2.1
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class,SqlSessionFactoryBean.class,MybatisProperties.class})
public class MdsMybatisComponentConfiguration {

    /**
     * 构建SqlSessionFactoryBean和ClassPathMapperScanner两个bean的Builder
     * 此处为默认实现：DefaultMdsMybatisComponentsBuilder
     */
    @Bean
    @ConditionalOnMissingBean
    public MdsMybatisComponentsBuilder defaultMdsMybatisComponentsBuilder() {
        return new DefaultMdsMybatisComponentsBuilder();
    }

    /**
     * MDS组件之Mybatis部分组件注册程序
     * 动态向Spring上下文中注册多个数据源的SqlSessionFactory、SqlSessionTemplate、XxxMapper等bean(beanName区分数据库)
     */
    @Bean
    public MdsMybatisComponentsRegistry mdsMybatisComponentAutoConfigurer(MdsMybatisComponentsBuilder mdsMybatisComponentsBuilder) {
        return new MdsMybatisComponentsRegistry(mdsMybatisComponentsBuilder);
    }

}
