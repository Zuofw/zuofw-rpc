package com.zuofw.rpc.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    /**
     * 普通请求
     */
    REQUEST((byte) 1),

    /**
     * 普通响应
     */
    RESPONSE((byte) 2),

    /**
     * 心跳
     */
    HEARTBEAT((byte) 3),
    ;
    private final byte value;
}
