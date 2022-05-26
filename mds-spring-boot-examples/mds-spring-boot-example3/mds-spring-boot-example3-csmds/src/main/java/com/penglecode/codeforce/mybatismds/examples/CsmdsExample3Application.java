package com.penglecode.codeforce.mybatismds.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mds.EnableMultiDataSource;
import org.springframework.boot.autoconfigure.mds.NamedDatabase;

/**
 * 基于shardingsphere-jdbc的Client-Sharding模式分库分表示例
 * 应用程序启动入口
 *
 * @author pengpeng
 * @since 2.1
 */
@SpringBootApplication
@EnableMultiDataSource({@NamedDatabase("product"), @NamedDatabase("order")})
public class CsmdsExample3Application {

    public static void main(String[] args) {
        SpringApplication.run(CsmdsExample3Application.class, args);
    }

}
