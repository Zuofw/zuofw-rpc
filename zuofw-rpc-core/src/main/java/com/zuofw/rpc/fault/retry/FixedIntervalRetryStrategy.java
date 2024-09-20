package com.zuofw.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.zuofw.rpc.model.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔时间重试策略
 * @author zuofw
 * @create 2024/9/18
 * @since 1.0.0
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{

    @Override
    public RPCResponse doRetry(Callable<RPCResponse> callable) throws Exception {
        // Retryer是重试的核心类，RetryerBuilder是构建Retryer的工厂类
        Retryer<RPCResponse> retryer = RetryerBuilder.<RPCResponse>newBuilder()
                // 重试条件，当发生Exception时重试
                .retryIfExceptionOfType(Exception.class)
                // 重试间隔时间策略，每次重试间隔3秒
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 重试停止策略，重试3次后停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 重试监听器，监听重试事件
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        // 重试日志
                        log.info("重试第{}次", attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}