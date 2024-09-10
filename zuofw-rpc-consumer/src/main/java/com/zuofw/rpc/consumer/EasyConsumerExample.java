package com.zuofw.rpc.consumer;

import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.proxy.ServiceProxyFactory;
import com.zuofw.rpc.common.model.User;
import com.zuofw.rpc.common.service.UserService;
import com.zuofw.rpc.utils.ConfigUtils;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/7
 * @since 1.0.0
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("zuofw");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("Get user success, name: " + newUser.getName());
        } else {
            System.out.println("Get user failed");
        }
        RPCConfig rpcConfig = ConfigUtils.loadConfig(RPCConfig.class, "rpc");
        System.out.println(rpcConfig);
    }
}