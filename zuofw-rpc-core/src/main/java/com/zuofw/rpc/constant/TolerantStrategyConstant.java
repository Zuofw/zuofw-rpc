package com.zuofw.rpc.constant;

/**
 * @Description 容错策略常量
 * @author zuofw
 */
public interface TolerantStrategyConstant {
    /**
     * 服务列表 用于容错策略
     */
    String SERVICE_LIST = "serviceList";

    /**
     * 当前正在调用的服务
     */
    String CURRENT_SERVICE = "currentService";

    /**
     * RPC Request
     */
    String RPC_REQUEST = "rpcRequest";
}