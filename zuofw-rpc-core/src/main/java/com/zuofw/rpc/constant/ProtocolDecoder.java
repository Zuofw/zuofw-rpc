package com.zuofw.rpc.constant;

import com.zuofw.rpc.compressor.Compressor;
import com.zuofw.rpc.factory.CompressorFactory;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ZMessage;
import com.zuofw.rpc.serialiizer.Serializer;
import com.zuofw.rpc.serialiizer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 〈解码〉
 *
 * @author zuofw
 * @create 2024/9/16
 * @since 1.0.0
 */
@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        log.info("解码消息");
        log.info("可读字节数：{}", byteBuf.readableBytes());
        // 一共18B，但是Length占4B，所以最少要14B
        if (byteBuf.readableBytes() < 14) {
            return;
        }
        byteBuf.markReaderIndex();
        log.info("读指针：{}", byteBuf.readerIndex());
        byte magic = byteBuf.readByte();
        if (magic != ProtocolConstant.MAGIC) {
            throw new RuntimeException("不支持的协议");
        }
        byte version = byteBuf.readByte();
        byte serialize = byteBuf.readByte();
        byte type = byteBuf.readByte();
        byte status = byteBuf.readByte();
        // 读取8个字节
        long requestId = byteBuf.readLong();
        byte compress = byteBuf.readByte();
        // 读取4个字节
        int bodyLength = byteBuf.readInt();
        log.info(String.valueOf( byteBuf.readableBytes()));
        // 如果可读字节数小于bodyLength，说明数据还没到齐，等待下一次读取
        if (byteBuf.readableBytes() < bodyLength) {
            // 重置读指针
            byteBuf.resetReaderIndex();
            return;
        }
        // todo 压缩解压缩
        byte[] body = new byte[bodyLength];
        byteBuf.readBytes(body);
        Compressor compressor = CompressorFactory.getInstance("gzip");
        byte[] unCompressBody = compressor.decompress(body);
        // 序列化
        SerializerEnum serializerEnum = SerializerEnum.getByKey(serialize);
        log.info("serializer种类是{}", serializerEnum.getValue());
        if (serializerEnum == null) {
            throw new RuntimeException("不支持的序列化器");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        // Class<?> 是一个通配符，表示任意类型
        Class<?> clazz = type == MessageType.REQUEST.getValue() ? RPCRequest.class : RPCResponse.class;
        Object bodyObj = serializer.deserialize(unCompressBody, clazz);
        ZMessage.Header header = new ZMessage.Header(magic, version, serialize, type, status, requestId, compress, bodyLength);
        ZMessage zMessage = new ZMessage(header, bodyObj);
        log.info("解码消息：{}", zMessage);
        list.add(zMessage);
    }
}