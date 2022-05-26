package org.springframework.boot.autoconfigure.mds;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 所有注册到Spring容器中的MDS组件集合
 * Map[key=数据库名称,value=对应该数据库所动态注册的各个组件Bean]
 *
 * @author pengpeng
 * @since 2.1
 */
public class MdsComponentBeans extends HashMap<String,Set<MdsComponentBean>> implements Cloneable {

    private MdsComponentBean defaultTransactionManager;

    public MdsComponentBeans() {
    }

    public MdsComponentBeans(Map<? extends String, ? extends Set<MdsComponentBean>> m) {
        super(m);
    }

    public MdsComponentBean getDefaultTransactionManager() {
        return defaultTransactionManager;
    }

    public void setDefaultTransactionManager(MdsComponentBean defaultTransactionManager) {
        this.defaultTransactionManager = defaultTransactionManager;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.insert(1, "defaultTransactionManager=" + defaultTransactionManager + (isEmpty() ? "" : ", "));
        return sb.toString();
    }

    @Override
    public MdsComponentBeans clone() {
        MdsComponentBeans newMdsComponentBeans = new MdsComponentBeans();
        newMdsComponentBeans.setDefaultTransactionManager(getDefaultTransactionManager());
        for(Entry<String,Set<MdsComponentBean>> entry : entrySet()) {
            newMdsComponentBeans.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
        }
        return newMdsComponentBeans;
    }

}
