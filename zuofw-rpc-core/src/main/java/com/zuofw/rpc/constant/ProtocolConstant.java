package com.zuofw.rpc.constant;

/**
 * 〈协议常量〉
 *
 * @author zuowei
 * @create 2024/9/15
 * @since 1.0.0
 */
public interface ProtocolConstant {

    /**
     * 消息头长度(单位字节)
     */
    int MESSAGE_HEADER_LENGTH = 18;

    /**
     * 魔数
     */
    byte MAGIC = 0x01;

    /**
     * 协议版本, 0x是16进制表示,转成10进制就是1
     */
    byte PROTOCOL_VERSION = 0x01;
}