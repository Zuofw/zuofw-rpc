package com.zuofw.rpc.Invoker;

import com.zuofw.rpc.model.RPCResponse;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈未处理的消息〉
 *
 * @author zuofw
 * @create 2024/9/16
 * @since 1.0.0
 */
// todo 双向的重试和负载选取需要用这个，还有超时处理，还有失败策略
public class UnprocessedRequests {
    private static final Map<Long, CompletableFuture<RPCResponse>> FUTURE_MAP = new ConcurrentHashMap<>();

    public static void put(long requestId, CompletableFuture<RPCResponse> future) {
        FUTURE_MAP.put(requestId, future);
    }

    /*
     * @description:  完成响应
     * @author zuofw
     * @date: 2024/9/16 14:49
     * @param rpcResponse
     */
    public static void complete(RPCResponse rpcResponse) {
        CompletableFuture<RPCResponse> future = FUTURE_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException("future is null. rpcResponse=" + rpcResponse);
        }
    }
}