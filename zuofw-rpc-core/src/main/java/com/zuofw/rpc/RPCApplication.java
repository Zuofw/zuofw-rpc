package com.zuofw.rpc;

import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.constant.RPCConstant;
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
            newRpcConfig = new RPCConfig();
        }
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
