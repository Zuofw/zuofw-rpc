package com.zuofw.rpc.spring.boot.provider.impl;

import com.zuofw.rpc.common.model.User;
import com.zuofw.rpc.common.service.UserService;
import com.zuofw.rpc.spring.boot.starter.annoation.ZuofwRPCService;
import org.springframework.stereotype.Service;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/14
 * @since 1.0.0
 */
@Service
@ZuofwRPCService
public class UserserviceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("服务端接收到请求，请求参数为：" + user);
        return user;
    }
}