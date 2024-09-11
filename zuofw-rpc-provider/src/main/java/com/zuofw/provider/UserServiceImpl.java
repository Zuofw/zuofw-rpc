package com.zuofw.provider;

import com.zuofw.rpc.common.model.User;
import com.zuofw.rpc.common.service.UserService;

/**
 * 〈provider提供〉
 *
 * @author zuowei
 * @create 2024/9/7
 * @since 1.0.0
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("UserServiceImpl.getUser username=" + user.getName());
        return user;
    }
}