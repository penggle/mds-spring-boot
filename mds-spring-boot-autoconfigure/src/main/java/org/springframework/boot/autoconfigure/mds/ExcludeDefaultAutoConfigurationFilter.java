package org.springframework.boot.autoconfigure.mds;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.core.annotation.Order;

import java.util.HashSet;
import java.util.Set;

/**
 * 禁用SpringBoot默认的DAL配置，具体来说就是：
 *      spring.autoconfigure.exclude[0]:org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
 *      spring.autoconfigure.exclude[1]:org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
 * @author pengpeng
 * @since 2.1
 */
//作为最后一个Filter来执行
@Order
public class ExcludeDefaultAutoConfigurationFilter implements AutoConfigurationImportFilter {

    private static final Set<String> EXCLUDE_AUTO_CONFIGURATIONS = new HashSet<>();

    static {
        EXCLUDE_AUTO_CONFIGURATIONS.add("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");
        EXCLUDE_AUTO_CONFIGURATIONS.add("org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration");
        EXCLUDE_AUTO_CONFIGURATIONS.add("org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration");
        EXCLUDE_AUTO_CONFIGURATIONS.add("com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration");
        EXCLUDE_AUTO_CONFIGURATIONS.add("org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration");
        EXCLUDE_AUTO_CONFIGURATIONS.add("org.apache.shardingsphere.sharding.spring.boot.ShardingRuleSpringBootConfiguration");
    }

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        int length = autoConfigurationClasses.length;
        boolean[] imports = new boolean[length];
        for(int i = 0; i < length ; i++) {
            imports[i] = !EXCLUDE_AUTO_CONFIGURATIONS.contains(autoConfigurationClasses[i]);
        }
        return imports;
    }
}
