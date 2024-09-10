package com.zuofw.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 动态代理工厂
 * 基于JDK动态代理
 *
 * @author zuowei
 * @create 2024/9/5
 * @since 1.0.0
 */
public class ServiceProxyFactory {
    /*
     * @description:  获取代理对象
     * @author bronya
     * @date: 2024/9/6 14:24
     * @param serviceClass
     * @return T
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        //使用Proxy.newProxyInstance()方法创建代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}