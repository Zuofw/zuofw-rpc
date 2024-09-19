package com.zuofw.rpc.factory;

import com.zuofw.rpc.fault.tolerant.TolerantStrategy;
import com.zuofw.rpc.spi.SPILoader;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/19
 * @since 1.0.0
 */
public class TolerantStrategyFactory {

    static {
        SPILoader.load(TolerantStrategy.class);
    }

    public static TolerantStrategy getInstance(String key) {
        return SPILoader.getInstance(TolerantStrategy.class, key);
    }
}