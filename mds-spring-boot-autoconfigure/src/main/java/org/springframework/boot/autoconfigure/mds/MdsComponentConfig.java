package org.springframework.boot.autoconfigure.mds;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * MDS组件配置
 *
 * @author pengpeng
 * @since 2.1
 */
public interface MdsComponentConfig {

    String getConfigPrefix();

    Class<?> getConfigPropertiesType();

    default String getConfigPrefixOf(String configKey) {
        return getConfigPrefix() + (StringUtils.hasText(configKey) ? "." + configKey : "");
    }

    /**
     * 从Spring环境上下文中获取绑定的属性配置(如果存在的话)
     * @param environment       - 不能为空
     * @param database          - 如果为空则表示不区分数据库
     * @return 如果返回null，则说明在Spring环境上下文中找不到与之绑定的属性配置
     */
    <T> T getBoundedConfigProperties(Environment environment, String database);

    /**
     * new指定配置类的空对象
     * @param <T>
     * @return
     */
    <T> T newDefaultConfigProperties();

    /**
     * 从Spring环境上下文中获取绑定的属性配置
     * @param environment
     * @param bindName
     * @param <T>
     * @return
     */
    default <T> T bindConfigProperties(Environment environment, String bindName) {
        T configInstance = newDefaultConfigProperties();
        return Binder.get(environment).bind(bindName, Bindable.ofInstance(configInstance)).orElse(configInstance);
    }

}
