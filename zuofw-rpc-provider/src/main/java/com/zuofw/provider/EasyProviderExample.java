package com.zuofw.provider;

import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.config.RegistryConfig;
import com.zuofw.rpc.model.ServiceMetaInfo;
import com.zuofw.rpc.registry.LocalRegistry;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.factory.RegistryFactory;
import com.zuofw.rpc.server.NettyServer;
import com.zuofw.rpc.common.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/7
 * @since 1.0.0
 */
@Slf4j
public class EasyProviderExample {
    public static void main(String[] args) {
        RPCApplication.init();

        String serviceName = UserService.class.getName();

        // provider向注册中心注册服务信息
        //向本地注册中心注册信息，方便Netty发送本地请求
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RPCConfig config = RPCApplication.getRpcConfig();
        RegistryConfig registryConfig = config.getRegistryConfig();
        // 获取一个注册中心实例
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        //设置服务信息
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(config.getServerHost());
        serviceMetaInfo.setServicePort(config.getServerPort());
        serviceMetaInfo.setServicePort(config.getServerPort());
        try {
            log.info("provider register service:{}", serviceMetaInfo.getServiceAddress());
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 启动Server服务
        NettyServer nettyServer = new NettyServer();
        log.info("服务已开启，端口为{}",config.getServerPort());
        nettyServer.start(config.getServerPort());

    }

}