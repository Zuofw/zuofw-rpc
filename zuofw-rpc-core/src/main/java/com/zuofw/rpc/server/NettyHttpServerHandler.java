package com.zuofw.rpc.server;

import com.zuofw.rpc.constant.MessageType;
import com.zuofw.rpc.constant.ProtocolConstant;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ZMessage;
import com.zuofw.rpc.registry.LocalRegistry;
import com.zuofw.rpc.serialiizer.JDKSerializer;
import com.zuofw.rpc.serialiizer.Serializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 〈NettyHandler〉
 *
 * @author zuowei
 * @create 2024/9/6
 * @since 1.0.0
 */
//为何要继承SimpleChannelInboundHandler，因为这个类是ChannelInboundHandler的子类，它会自动释放资源
@Slf4j
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<ZMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ZMessage zMessage) throws Exception {
        log.info("Server received message: {}", zMessage);  // 添加日志以确认服务端是否收到请求
        if (zMessage.getHeader().getType() != MessageType.REQUEST.getValue()) {
            return;
        }
        System.out.println("reveive request:" + zMessage.getHeader().getType());
        try {
            if(zMessage.getHeader().getType() != MessageType.REQUEST.getValue()) {
                return;
            }

            RPCRequest rpcRequest = (RPCRequest) zMessage.getBody();
            ZMessage.Header.HeaderBuilder headerBuilder = ZMessage.Header.builder()
                    .serialize(zMessage.getHeader().getSerialize())
                    .compress(zMessage.getHeader().getCompress())
                    .requestId(zMessage.getHeader().getRequestId())
                    .magic(ProtocolConstant.MAGIC)
                    .type(MessageType.RESPONSE.getValue());
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                RPCResponse rpcResponse = new RPCResponse();
                rpcResponse.setRequestId(zMessage.getHeader().getRequestId());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("success");
                ZMessage response = ZMessage.builder()
                        .body(rpcResponse)
                        .header(headerBuilder.build())
                        .build();
                channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                log.info("Server send message: {}", response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            ReferenceCountUtil.release(zMessage);
        }
    }

}