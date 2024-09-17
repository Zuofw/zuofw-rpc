package com.zuofw.rpc.Invoker;


import com.zuofw.rpc.model.RPCResult;
import com.zuofw.rpc.model.RPCRequest;

public interface Invoker {

    /*
     * @description:  发送请求
     * @author zuofw
     * @date: 2024/9/16 13:30
     * @param request
     * @return com.zuofw.rpc.model.RPCResult
     */
    RPCResult invoke(RPCRequest request) throws Exception;
}
