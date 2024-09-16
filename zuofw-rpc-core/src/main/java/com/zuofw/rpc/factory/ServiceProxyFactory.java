package com.zuofw.rpc.factory;

import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.proxy.MockServiceProxy;
import com.zuofw.rpc.proxy.ServiceProxy;

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
        if(RPCApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
    /*
     * @description:  获取Mock代理对象，用于测试环境下的Mock数据返回，不会真正调用远程服务，而是返回Mock数据，用于测试环境下的单元测试等场景
     * @author bronya
     * @date: 2024/9/10 20:44
     * @param null
     * @return null
     */

    private static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }
}