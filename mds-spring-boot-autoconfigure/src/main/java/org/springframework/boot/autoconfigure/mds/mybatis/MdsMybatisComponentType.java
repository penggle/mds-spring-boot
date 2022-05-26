package org.springframework.boot.autoconfigure.mds.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.mds.MdsComponentType;

import java.lang.reflect.Proxy;

/**
 * MDS之Mybatis组件类型枚举
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public enum MdsMybatisComponentType implements MdsComponentType {

    MYBATIS_PROPERTIES(MybatisProperties.class),
    SQL_SESSION_FACTORY(SqlSessionFactory.class),
    SQL_SESSION_TEMPLATE(SqlSessionTemplate.class),
    MYBATIS_MAPPER(Proxy.class) {
        @Override
        public String getComponentBeanName(String database) {
            throw new UnsupportedOperationException("Please use static method 'getComponentBeanName' of MdsComponentType!");
        }
    };

    private final Class<?> componentBeanType;

    MdsMybatisComponentType(Class<?> componentBeanType) {
        this.componentBeanType = componentBeanType;
    }

    @Override
    public <T> Class<T> getComponentBeanType() {
        return (Class<T>) componentBeanType;
    }

}
