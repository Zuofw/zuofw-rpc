package com.zuofw.rpc.model;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/16
 * @since 1.0.0
 */
@Slf4j
public class FutureResult implements RPCResult {


    private final CompletableFuture<?> future;

    public FutureResult(CompletableFuture<?> future) {
        this.future = future;
    }

    @Override
    public boolean isSuccess() {
        return !future.isCompletedExceptionally();
    }

    @Override
    public Object getData() {
        try {
            return future.get();
        } catch (Exception e) {
            log.info("getData error.", e);
        }
        return null;
    }
}