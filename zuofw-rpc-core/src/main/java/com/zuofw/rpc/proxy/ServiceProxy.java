package com.zuofw.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.constant.RPCConstant;
import com.zuofw.rpc.model.RPCRequst;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ServiceMetaInfo;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.registry.RegistryFactory;
import com.zuofw.rpc.serialiizer.JDKSerializer;
import com.zuofw.rpc.serialiizer.Serializer;
import com.zuofw.rpc.serialiizer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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
        RPCRequst rpcRequest = RPCRequst.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 序列化请求
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者请求地址
            RPCConfig rpcConfig = RPCApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            // 构造请求
            String serviceName = method.getDeclaringClass().getName();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RPCConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos)) {
                throw new RuntimeException("暂无可用服务提供者");
            }
            // 暂时先取第一个
            ServiceMetaInfo metaInfo = serviceMetaInfos.get(0);

            // 发送请求
            try (HttpResponse httpResponse = HttpRequest.post(metaInfo.getServiceAddress()).body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化响应
                RPCResponse rpcResponse = serializer.deserialize(result, RPCResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}