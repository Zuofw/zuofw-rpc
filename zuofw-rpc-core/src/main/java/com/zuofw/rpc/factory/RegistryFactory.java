package com.zuofw.rpc.factory;

import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.registry.ZooKeeperRegistry;
import com.zuofw.rpc.spi.SPILoader;

/**
 * 〈注册中心工厂类〉
 *
 * @author zuowei
 * @create 2024/9/11
 * @since 1.0.0
 */
public class RegistryFactory {
    static {
        // 加载注册中心实现类
        SPILoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
     public static final Registry DEFAULT_REGISTRY = new ZooKeeperRegistry();

     public static Registry getInstance(String key) {
        return SPILoader.getInstance(Registry.class, key);
    }

}