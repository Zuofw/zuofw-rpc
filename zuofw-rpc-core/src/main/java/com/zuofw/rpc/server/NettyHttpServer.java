package com.zuofw.rpc.server;

import com.zuofw.rpc.constant.ProtocolDecoder;
import com.zuofw.rpc.constant.ProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyHttpServer implements HttpServer {

    @Override
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * boss线程组用于处理连接工作，worker线程组用于数据处理
             * 依次的结构是 group -> channel -> childHandler -> handler
             * group 用于处理连接，channel 用于处理数据，childHandler 用于处理连接的数据，handler 用于处理数据的
             * 所属关系:一个 group 可以有多个 channel，一个 channel 可以有多个 childHandler，一个 childHandler 可以有多个 handler
             * 一个 channel 只能有一个 childHandler，一个 childHandler 可以有多个 handler
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.TRACE))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
//                                    .addLast(new HttpServerCodec())
//                                    .addLast(new HttpObjectAggregator(65536))
//                                    .addLast(new ChunkedWriteHandler())
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new ProtocolDecoder())
                                    .addLast(new NettyHttpServerHandler());
//                                    .addLast(new TestNettyHandler());
                            // todo 接收消息，将消息先编码，然后解码成ZMessage格式，最后交由NettyHttpServerHandler处理
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server is now listening on port " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyHttpServer().start(8080);
    }
}