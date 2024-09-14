package com.zuofw.rpc;

import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.config.RegistryConfig;
import com.zuofw.rpc.constant.RPCConstant;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.registry.RegistryFactory;
import com.zuofw.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC应用
 * 相当于holder ,存放了项目全局用到的变量，双检锁实现单例
 */
@Slf4j
public class RPCApplication {
    private static volatile RPCConfig rpcConfig;

    public static void init(RPCConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc application init success,config:{}", rpcConfig);
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        //进行注册中心的初始化
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init success,config:{}", registryConfig);
    }

    /**
     * 初始化
     */
    public static void init() {
        RPCConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RPCConfig.class, RPCConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 读取配置文件失败，使用默认配置
            System.out.println("读取文件失败");
            log.info("读取文件配置失败");
            newRpcConfig = new RPCConfig();
        }
        System.out.println("newRpcConfig: " + newRpcConfig);
        init(newRpcConfig);
    }

    /**
     * 获取配置
     */
    public static RPCConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RPCApplication.class) {
                if (rpcConfig == null) {
                    init();
                }

            }
        }
        return rpcConfig;
    }

}
