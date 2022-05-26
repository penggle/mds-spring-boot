package com.penglecode.codeforce.mybatismds.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mds.EnableMultiDataSource;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

/**
 * 普通常规的(General)多数据源示例(包括server-proxy模式的分库分表，例如mycat、shardingsphere-proxy)
 * 应用程序启动入口
 *
 * @author pengpeng
 * @since 2.1
 */
@SpringBootApplication
@EnableMultiDataSource({@NamedDatabase("product"), @NamedDatabase("order")})
public class GmdsExample3Application {

    public static void main(String[] args) {
        GmdsExample3Application.class.getDeclaredMethods();
        SpringApplication.run(GmdsExample3Application.class, args);
    }

}
