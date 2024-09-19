package com.zuofw.rpc.fault.tolerant;

import com.zuofw.rpc.model.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 快速失败
 *
 * @author zuowei
 * @create 2024/9/19
 * @since 1.0.0
 */
@Slf4j
public class FailFastTolerantStrategy implements TolerantStrategy{

    @Override
    public RPCResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("快速失败了",e);
        throw new RuntimeException("快速失败了",e);
    }
}