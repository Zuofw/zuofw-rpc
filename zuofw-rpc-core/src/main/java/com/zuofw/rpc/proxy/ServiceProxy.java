package com.zuofw.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.model.RPCRequst;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.serialiizer.JDKSerializer;
import com.zuofw.rpc.serialiizer.Serializer;
import com.zuofw.rpc.serialiizer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理实现类
 *
 * @author zuowei
 * @create 2024/9/6
 * @since 1.0.0
 */

public class ServiceProxy implements InvocationHandler {

    final Serializer serializer = SerializerFactory.getInstance(RPCApplication.getRpcConfig().getSerializer());
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        RPCRequst rpcRequst = RPCRequst.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequst);
            // 发送请求
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                RPCResponse response = serializer.deserialize(result, RPCResponse.class);
                return response.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}