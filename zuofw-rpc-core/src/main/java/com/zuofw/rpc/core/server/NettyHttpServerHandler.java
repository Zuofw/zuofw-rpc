package com.zuofw.rpc.core.server;

import com.zuofw.rpc.core.model.RPCRequst;
import com.zuofw.rpc.core.model.RPCResponse;
import com.zuofw.rpc.core.registry.LocalRegistry;
import com.zuofw.rpc.core.serialiizer.JDKSerializer;
import com.zuofw.rpc.core.serialiizer.Serializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

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
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        JDKSerializer serializer = new JDKSerializer();
        System.out.println("reveive request:" + fullHttpRequest.method() + " " + fullHttpRequest.uri());
        //异步处理HTTP请求
        /**
         * Netty本身的设计是异步和事件驱动的，
         * 这意味着即使你在channelRead0方法中执行同步操作，
         * Netty也会在后台以异步方式处理I/O事件。
         */
        byte[] bytes = new byte[fullHttpRequest.content().readableBytes()];
        //将请求参数反序列化
        fullHttpRequest.content().readBytes(bytes);
        RPCRequst rpcRequest = null;

        try {
            rpcRequest = serializer.deserialize(bytes, RPCRequst.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RPCResponse rpcResponse = new RPCResponse();
        if(rpcRequest == null) {
            rpcResponse.setMessage("rpcRequest is null");
            doResponse(channelHandlerContext, rpcResponse, serializer);
            return;
        }
        try {
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
            rpcResponse.setData(result);
            rpcResponse.setDataType(method.getReturnType());
            rpcResponse.setMessage("success");
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setMessage(e.getMessage());
            rpcResponse.setException(e);
        }
        doResponse(channelHandlerContext, rpcResponse, serializer);
    }
    private void doResponse(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse, Serializer serializer) throws IOException {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(serializer.serialize(rpcResponse))
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}