package com.zuofw.rpc.spring.consumer;

import com.zuofw.rpc.common.model.User;
import com.zuofw.rpc.common.service.UserService;
import com.zuofw.rpc.spring.boot.starter.annoation.ZuofwRPCReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/14
 * @since 1.0.0
 */
@Service
public class UserServiceImpl {
    @ZuofwRPCReference
    private UserService userService;

    public void sayHello(String name) {
        User user = new User();
        user.setName("zuofw");
        User resultUser = userService.getUser(user);
        System.out.println("consumer get User:" + resultUser.getName());
    }
}