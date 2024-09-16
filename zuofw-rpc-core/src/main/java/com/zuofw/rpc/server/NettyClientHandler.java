package com.zuofw.rpc.server;

import com.zuofw.rpc.Invoker.UnprocessedRequests;
import com.zuofw.rpc.constant.MessageType;
import com.zuofw.rpc.constant.SerializerEnum;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ZMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<ZMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext context, ZMessage requestMsg) {
        try {
            log.info("client receive msg: [{}]", requestMsg);
            if (requestMsg.getHeader().getType() == MessageType.RESPONSE.getValue()) {
                RPCResponse response = (RPCResponse) requestMsg.getBody();
                UnprocessedRequests.complete(response);
            }
        } finally {
            ReferenceCountUtil.release(requestMsg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 心跳
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                ZMessage rpcMessage = new ZMessage();
                ZMessage.Header header = new ZMessage.Header();
                header.setSerialize((byte) SerializerEnum.JDK.getKey());
                header.setCompress((byte) 0);
                header.setType(MessageType.HEARTBEAT.getValue());
                rpcMessage.setHeader(header);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}