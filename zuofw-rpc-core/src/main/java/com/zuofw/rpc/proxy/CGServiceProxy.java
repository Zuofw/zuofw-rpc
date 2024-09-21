package com.zuofw.rpc.proxy;

import com.zuofw.rpc.Invoker.Invoker;
import com.zuofw.rpc.factory.InvokerFactory;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.RPCResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/21
 * @since 1.0.0
 */
@Slf4j
public class CGServiceProxy implements MethodInterceptor {
    private final Invoker invoker = InvokerFactory.getInstance("netty");

    public Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 构造请求
        RPCRequest rpcRequest = RPCRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        RPCResult invoke = invoker.invoke(rpcRequest);
        log.info("invoke result:{}", invoke.getData());
        RPCResponse response = (RPCResponse) invoke.getData();
        return response.getData();
    }
}