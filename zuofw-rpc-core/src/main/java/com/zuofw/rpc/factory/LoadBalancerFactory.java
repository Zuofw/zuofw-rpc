package com.zuofw.rpc.factory;

import com.zuofw.rpc.constant.LoadBalanceKeys;
import com.zuofw.rpc.loadbalance.LoadBalancer;
import com.zuofw.rpc.spi.SPILoader;

/**
 * 〈〉
 *
 * @author zuofw
 * @create 2024/9/17
 * @since 1.0.0
 */
public class LoadBalancerFactory {
    static {
        SPILoader.load(LoadBalancer.class);
    }

    public static LoadBalancer getInstance(String key) {
        return SPILoader.getInstance(LoadBalancer.class, key);
    }

}