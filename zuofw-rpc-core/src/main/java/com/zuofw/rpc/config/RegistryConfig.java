package com.zuofw.rpc.config;

import lombok.Data;

/**
 * 〈注册中心的配置〉
 *
 * @author zuowei
 * @create 2024/9/11
 * @since 1.0.0
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类型
     */
    private String registry = "zookeeper";

    /**
     * 地址
     */
    private String address = "http://192.168.61.190:2181";

    /**
     * 用户名
     */
    private String username;


    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间
     */
    private Long timeout = 100000L;
}