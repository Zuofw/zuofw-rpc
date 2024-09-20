package com.zuofw.rpc.fault.retry;

import com.zuofw.rpc.model.RPCResponse;

import java.util.concurrent.Callable;

/**
 * 〈不重试机制〉
 *
 * @author zuofw
 * @create 2024/9/18
 * @since 1.0.0
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception {
        return callable.call();
    }
}