package org.springframework.boot.autoconfigure.mds;

import org.springframework.util.ClassUtils;

/**
 * MDS组件类型
 *
 * @author pengpeng
 * @since 2.1
 */
public interface MdsComponentType {

    /**
     * 获取组件Bean注册类型
     * @param <T>
     * @return
     */
    <T> Class<T> getComponentBeanType();

    /**
     * 获取组件Bean注册名称
     * @param database
     * @return
     */
    default String getComponentBeanName(String database) {
        return getComponentBeanName(database, getComponentBeanType());
    }

    static String getComponentBeanName(String database, Class<?> beanType) {
        return database + beanType.getSimpleName();
    }

    static String getComponentBeanName(String database, String beanTypeName) {
        return database + ClassUtils.getShortName(beanTypeName);
    }

}
