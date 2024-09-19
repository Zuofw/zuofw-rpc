package com.zuofw.rpc.fault.retry;

import com.zuofw.rpc.model.RPCResponse;

import java.util.concurrent.Callable;

/**
 * 容错重试策略
 *
 * @author zuowei
 * @create 2024/9/17
 * @since 1.0.0
 */
public interface RetryStrategy {

        /*
         * @description:   重试,参数为Callable，callable代表一个任务，返回值为RPCResponse，代表任务执行结果
         * @author bronya
         * @date: 2024/9/17 17:09
         * @param callable
         * @return com.zuofw.rpc.model.RPCResponse
         */
        RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception;
}