package com.zuofw.rpc.config;

import lombok.Data;

/**
 * 〈RPC全局配置类〉
 *
 * @author zuowei
 * @create 2024/9/10
 * @since 1.0.0
 */
@Data
public class RPCConfig {
     private String name = "zuofw-rpc";
     private String version = "1.0";
     private String serverHost = "localhost";
     private int serverPort = 8080;

     private boolean mock = false;

}