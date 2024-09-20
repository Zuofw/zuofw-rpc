package com.zuofw.rpc.spring.boot.starter.bootstrap;

import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.config.RegistryConfig;
import com.zuofw.rpc.model.ServiceMetaInfo;
import com.zuofw.rpc.registry.LocalRegistry;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.factory.RegistryFactory;
import com.zuofw.rpc.spring.boot.starter.annoation.ZuofwRPCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author zuofw
 */
@Slf4j
public class RPCProviderBootstrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        ZuofwRPCService rpcService = beanClass.getAnnotation(ZuofwRPCService.class);
        if (rpcService != null) {
            // 需要注册服务
            // 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RPCConfig rpcConfig = RPCApplication.getRpcConfig();
            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
