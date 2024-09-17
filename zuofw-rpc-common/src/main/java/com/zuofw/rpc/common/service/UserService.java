package com.zuofw.rpc.common.service;

import com.zuofw.rpc.common.model.User;

public interface UserService {
    User getUser(User user);
    /*
     * @description:   用于测试默认方法
     * @author zuofw
     * @date: 2024/9/10 20:48
     * @return short
     */
    default short getNumber() {
        return 1;
    }
}
