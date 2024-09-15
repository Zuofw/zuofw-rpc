package com.zuofw.rpc.spring.boot.starter.bootstrap;

import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.server.NettyHttpServer;
import com.zuofw.rpc.spring.boot.starter.annoation.EnableZuofwRpc;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author houzuofw
 */
@Slf4j
public class RPCInitBootStrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring初始化执行时候，初始化Rpc框架
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableZuofwRpc.class.getName()).get("needServer");

        // Rpc框架初始化（配置和注册中心）
        RPCApplication.init();

        final RPCConfig rpcConfig = RPCApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            NettyHttpServer nettyHttpServer = new NettyHttpServer();
            new Thread(() -> {
                nettyHttpServer.start(rpcConfig.getServerPort());
            }).start();
        } else {
            log.info("Rpc server is not started");
        }
    }
}
