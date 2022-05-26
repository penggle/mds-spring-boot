package com.penglecode.codeforce.mybatismds.examples.common.config;


import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author pengpeng
 * @since 2.1
 */
@Validated
public class MyExampleProperties {

    @NotBlank(message="主机名不能为空!")
    private String host;

    @NotNull(message="端口号不能为空!")
    @Min(value=0, message="端口号不能小于0")
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
