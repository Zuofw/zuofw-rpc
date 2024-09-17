package com.zuofw.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 〈自定义的消息格式〉
 *
 * @author zuofw
 * @create 2024/9/15
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 使用T泛型，可以接收任意类型的消息体
public class ZMessage<T> {

    /**
     * 请求头
     */
    private Header header;

    /**
     * body
     */
    private T body;

    /**
     * 使用静态内部类，定义请求头,优点是可以直接通过ZMessage.Header访问，方便解耦，内部类也不会出现外部引用
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;

        /**
         * 版本
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serialize;

        /**
         * 消息类型
         */
        private byte type;

        /**
         * 消息状态
         */
        private byte status;

        /**
         * 请求id
         */
        private long requestId;



        /**
         * 压缩格式
         */
        private byte compress;

        /**
         * 消息体长度
         */

        private int bodyLength;

    }
}