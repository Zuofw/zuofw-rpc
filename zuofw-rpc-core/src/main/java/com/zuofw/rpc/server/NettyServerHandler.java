package com.zuofw.rpc.server;

import com.zuofw.rpc.constant.MessageType;
import com.zuofw.rpc.constant.ProtocolConstant;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ZMessage;
import com.zuofw.rpc.registry.LocalRegistry;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Id;

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
public class NettyServerHandler extends SimpleChannelInboundHandler<ZMessage> {

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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，关闭连接");
                ctx.close();
            }
        } else {
            // 调用父类的userEventTriggered方法，继续传播事件
            super.userEventTriggered(ctx, evt);
        }
    }
}