package com.zuofw.rpc.fault.tolerant;



import com.zuofw.rpc.model.RPCResponse;

import java.util.Map;

/**
 * @author zuofw
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return
     */
    RPCResponse doTolerant(Map<String, Object> context, Exception e);
}
