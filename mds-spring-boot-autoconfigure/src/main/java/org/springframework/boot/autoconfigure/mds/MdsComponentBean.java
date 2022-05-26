package org.springframework.boot.autoconfigure.mds;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * MDS组件Bean
 *
 * @author pengpeng
 * @since 2.1
 */
@SuppressWarnings("unchecked")
public class MdsComponentBean {

    private final String database;

    private final MdsComponentType componentType;

    private final String componentBeanName;

    private final Object componentBean;

    /** 是否是本框架自动创建的 */
    private final boolean autoCreated;

    public MdsComponentBean(MdsComponentType componentType, String componentBeanName, Object componentBean, boolean autoCreated) {
        this(null, componentType, componentBeanName, componentBean, autoCreated);
    }

    public MdsComponentBean(String database, MdsComponentType componentType, String componentBeanName, Object componentBean, boolean autoCreated) {
        this.database = database;
        this.componentType = componentType;
        this.componentBeanName = componentBeanName;
        this.componentBean = componentBean;
        this.autoCreated = autoCreated;
    }

    public String getDatabase() {
        return database;
    }

    public MdsComponentType getComponentType() {
        return componentType;
    }

    public String getComponentBeanName() {
        return componentBeanName;
    }

    public <T> T getComponentBean() {
        return (T) componentBean;
    }

    public boolean isAutoCreated() {
        return autoCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MdsComponentBean)) return false;
        MdsComponentBean that = (MdsComponentBean) o;
        return Objects.equals(componentBeanName, that.componentBeanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentBeanName);
    }

    @Override
    public String toString() {
        return StringUtils.hasText(database) ? componentBeanName + "@" + database : componentBeanName;
    }

}
