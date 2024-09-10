package com.zuofw.rpc.provider;

import com.zuofw.rpc.registry.LocalRegistry;
import com.zuofw.rpc.server.NettyHttpServer;
import com.zuofw.rpc.common.service.UserService;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/7
 * @since 1.0.0
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.start(8080);
    }

}