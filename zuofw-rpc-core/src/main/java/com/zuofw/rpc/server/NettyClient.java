package com.zuofw.rpc.server;

import cn.hutool.core.util.StrUtil;
import com.zuofw.rpc.constant.ProtocolDecoder;
import com.zuofw.rpc.constant.ProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 〈〉
 *
 * @author zuowei
 * @create 2024/9/16
 * @since 1.0.0
 */
@Slf4j
public class NettyClient {
    private final Bootstrap bootstrap;

    /**
     * 连接地址和channel的映射
     */
    private static final Map<SocketAddress, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static NettyClient instance = null;


    private NettyClient() {
        bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                // todo 日志级别
                .handler(new LoggingHandler(LogLevel.TRACE))
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline()
                                // 5秒进行一次心跳检测，如果5秒内没有收到发送的数据，则触发一次userEventTriggered方法
                                .addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
                                .addLast(new ProtocolEncoder())
                                .addLast(new ProtocolDecoder())
                                .addLast(new NettyClientHandler());
                    }
                });
        // 发送，先解码成ZMessage，然后编码成ByteBuf，最后发送
    }
    /*
     * @description:  获取单例
     * @author bronya
     * @date: 2024/9/16 14:23
     * @return com.zuofw.rpc.server.NettyClient
     */
    public static NettyClient getInstance() {
        if(instance == null) {
            synchronized (NettyClient.class) {
                if(instance == null) {
                    instance = new NettyClient();
                }
            }
        }
        return instance;
    }

    /*
     * @description:   获取连接的channel，如果没有则连接地址并返回channel，如果有则直接返回channel，如果channel不活跃则重新连接并返回channel
     * @author bronya
     * @date: 2024/9/16 14:23
     * @param address
     * @return io.netty.channel.Channel
     */
    public Channel getChannel(SocketAddress address) {
        Channel channel = CHANNEL_MAP.get(address);
        if (channel == null || !channel.isActive()) {
            channel = connect(address);
            CHANNEL_MAP.put(address, channel);
        }
        return channel;
    }

    /**
     * 尝试去建立连接
     * todo 可以看看
     *
     * @param address 地址
     * @return channel
     */
    private Channel connect(SocketAddress address) {
        try {
            log.info("Try to connect server [{}]", address);
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            ChannelFuture connect = bootstrap.connect(address);
            connect.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    // complete方法会触发completableFuture.get()方法
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalStateException(StrUtil.format("connect fail. address:", address));
                }
            });
            // 10s超时
            return completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException(address + " connect fail.", ex);
        }
    }
}