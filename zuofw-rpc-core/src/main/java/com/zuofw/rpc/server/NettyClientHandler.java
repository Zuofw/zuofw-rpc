package com.zuofw.rpc.server;

import com.zuofw.rpc.Invoker.UnprocessedRequests;
import com.zuofw.rpc.RPCApplication;
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


@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<ZMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext context, ZMessage requestMsg) {
        try {
            log.info("client receive msg: [{}]", requestMsg);
            if (requestMsg.getHeader().getType() == MessageType.RESPONSE.getValue()) {
                RPCResponse response = (RPCResponse) requestMsg.getBody();
                UnprocessedRequests.complete(response);
                log.info("成功收到响应，已经删除未处理的请求");
            }
        } finally {
            ReferenceCountUtil.release(requestMsg);
        }
    }

    /**
     * 这个是用来处理心跳的
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 如果超过设定的时间没有写请求，就会触发WRITE_IDLE事件
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("心跳发送 [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                // 触发事件之后，我们应该发送我们的心跳包
                ZMessage rpcMessage = new ZMessage();
                ZMessage.Header header = new ZMessage.Header();
                header.setSerialize((byte)SerializerEnum.getByValue(RPCApplication.getRpcConfig().getSerializer()).getKey());
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
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}