package com.zuofw.consumer;

import cn.hutool.http.HttpRequest;
import com.zuofw.rpc.model.RPCRequst;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.serialiizer.JDKSerializer;
import com.zuofw.rpc.common.model.User;
import com.zuofw.rpc.common.service.UserService;

import java.io.IOException;

/**
 * 〈静态实现〉
 *
 * @author zuowei
 * @create 2024/9/7
 * @since 1.0.0
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        JDKSerializer serializer = new JDKSerializer();

        RPCRequst rpcRequst = RPCRequst.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequst);
            byte[] result;
            String url = "http://localhost:8080";
            result = HttpRequest.post(url).body(bodyBytes).execute().bodyBytes();
            RPCResponse rpcResponse = serializer.deserialize(result, RPCResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}