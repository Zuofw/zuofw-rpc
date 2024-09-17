package com.zuofw.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zuofw.rpc.Invoker.Invoker;
import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.constant.RPCConstant;
import com.zuofw.rpc.factory.InvokerFactory;
import com.zuofw.rpc.factory.RegistryFactory;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.RPCResult;
import com.zuofw.rpc.model.ServiceMetaInfo;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.serialiizer.Serializer;
import com.zuofw.rpc.serialiizer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 动态代理实现类
 *
 * @author zuofw
 * @create 2024/9/6
 * @since 1.0.0
 */

@Slf4j
public class ServiceProxy implements InvocationHandler {

    final Invoker invoker = InvokerFactory.getInstance("netty");
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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