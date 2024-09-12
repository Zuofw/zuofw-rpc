package com.zuofw.rpc.config;

import com.zuofw.rpc.serialiizer.SerializerFactory;
import com.zuofw.rpc.serialiizer.SerializerKeys;
import lombok.Data;

/**
 * 〈RPC全局配置类,如果没有覆盖默认是提供服务的一方所对应的配置〉
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
     private Integer serverPort = 8080;

     private boolean mock = false;
     /**
      * 序列化器
      */
     private String serializer = SerializerKeys.JDK;

     private RegistryConfig registryConfig;

}