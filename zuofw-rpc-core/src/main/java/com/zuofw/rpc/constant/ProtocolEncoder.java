package com.zuofw.rpc.constant;

import com.zuofw.rpc.model.ZMessage;
import com.zuofw.rpc.serialiizer.Serializer;
import com.zuofw.rpc.serialiizer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * 〈协议序列化器〉
 * @author zuofw
 * @create 2024/9/15
 * @since 1.0.0
 */
@Slf4j
public class ProtocolEncoder extends MessageToByteEncoder<ZMessage<?>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ZMessage<?> zMessage, ByteBuf byteBuf) throws IOException {
        if(zMessage == null || zMessage.getHeader() == null){
            return;
        }
        log.info("Message{}",zMessage);
        ZMessage.Header header = zMessage.getHeader();
        byteBuf.writeByte(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialize());
        byteBuf.writeByte(header.getType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        byteBuf.writeByte(header.getCompress());
        SerializerEnum serializerEnum = SerializerEnum.getByKey(header.getSerialize());
        if(serializerEnum == null) {
            throw new RuntimeException("不支持的序列化器");
        }
        log.info("serializer的种类是{}", serializerEnum.getValue());
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(zMessage.getBody());
        log.info("Serialized message body length: {}", bodyBytes.length);  // 添加日志以确认序列化后的消息长度
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
    }
}