package com.zuofw.rpc.factory;

import com.zuofw.rpc.Invoker.Invoker;
import com.zuofw.rpc.Invoker.NettyInvoker;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.spi.SPILoader;
import org.checkerframework.checker.units.qual.N;

/**
 * 〈〉
 *
 * @author zuofw
 * @create 2024/9/16
 * @since 1.0.0
 */
public class InvokerFactory {
    static {
        SPILoader.load(Invoker.class);
    }
    public static final Invoker DEFAULT_INVOKER = new NettyInvoker();

    public static Invoker getInstance(String key) {
        return SPILoader.getInstance(Invoker.class, key);
    }

}