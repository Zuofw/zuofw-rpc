package com.zuofw.rpc.Invoker;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.constant.*;
import com.zuofw.rpc.factory.RegistryFactory;
import com.zuofw.rpc.model.*;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.server.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 〈Netty实现的发送代理〉
 *
 * @author zuowei
 * @create 2024/9/16
 * @since 1.0.0
 */
@Slf4j
public class NettyInvoker implements Invoker{

    private final NettyClient nettyClient = NettyClient.getInstance();
    @Override
    public RPCResult invoke(RPCRequest request) throws Exception {
        // 从注册中心获取服务提供者请求地址
        RPCConfig rpcConfig = RPCApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        // 构造请求
        String serviceName = request.getServiceName();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RPCConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无可用服务提供者");
        }
        // 暂时先取第一个
        // todo 负载均衡待做
        ServiceMetaInfo metaInfo = serviceMetaInfos.get(0);
        log.info("address{}",metaInfo.getServiceAddress());
        InetSocketAddress socketAddress = new InetSocketAddress( metaInfo.getServiceHost(), metaInfo.getServicePort());
        // 打印url
        log.info("service url:{}", socketAddress);
        Channel channel = nettyClient.getChannel(socketAddress);
        if(channel.isActive()) {
            CompletableFuture<RPCResponse> resultFuture = new CompletableFuture<>();
            // 构造消息
            ZMessage message = buildMessage(request);
            UnprocessedRequests.put(message.getHeader().getRequestId(), resultFuture);
            channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()) {
                    System.out.println("client send message: [" + message + "]");
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    System.err.println("send failed:" + future.cause());
                }
            });
            return new FutureResult(resultFuture);
        } else {
            throw new RuntimeException("channel is not active. address=" + socketAddress);
        }
    }
    /*
     * @description:构建消息
     * @author bronya
     * @date: 2024/9/16 14:46
     * @param request
     * @return com.zuofw.rpc.protocol.ZMessage<?>
     */
    private ZMessage<?> buildMessage(RPCRequest request) {
        // todo 这里可以设计成从用户的协议配置中获取
        ZMessage.Header header = new ZMessage.Header();
        header.setMagic(ProtocolConstant.MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        // todo 压缩类型
        header.setSerialize( (byte) SerializerEnum.getByValue(RPCApplication.getRpcConfig().getSerializer()).getKey());
        header.setType(MessageType.REQUEST.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setCompress((byte) 0);
        header.setStatus((byte) 0);
        header.setBodyLength(request.toString().getBytes().length);
        return ZMessage.builder().header(header).body(request).build();
    }
}