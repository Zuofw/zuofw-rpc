package com.zuofw.rpc.factory;

import com.zuofw.rpc.fault.retry.RetryStrategy;
import com.zuofw.rpc.spi.SPILoader;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/18
 * @since 1.0.0
 */
public class RetryStrategyFactory {
    static {
        // 加载重试策略实现类
        SPILoader.load(RetryStrategy.class);
    }

    public static RetryStrategy getInstance(String key) {
        return SPILoader.getInstance(RetryStrategy.class, key);
    }

}