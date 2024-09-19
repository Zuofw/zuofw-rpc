package com.zuofw.rpc.Invoker;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.zuofw.rpc.RPCApplication;
import com.zuofw.rpc.config.RPCConfig;
import com.zuofw.rpc.constant.*;
import com.zuofw.rpc.factory.LoadBalancerFactory;
import com.zuofw.rpc.factory.RegistryFactory;
import com.zuofw.rpc.factory.RetryStrategyFactory;
import com.zuofw.rpc.factory.TolerantStrategyFactory;
import com.zuofw.rpc.fault.retry.RetryStrategy;
import com.zuofw.rpc.fault.tolerant.TolerantStrategy;
import com.zuofw.rpc.loadbalance.LoadBalancer;
import com.zuofw.rpc.model.*;
import com.zuofw.rpc.registry.Registry;
import com.zuofw.rpc.server.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class NettyInvoker implements Invoker {

    private final NettyClient nettyClient = NettyClient.getInstance();
    private final LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(LoadBalanceKeys.RANDOM);

    @Override
    public RPCResult invoke(RPCRequest request) throws Exception {
        // 从注册中心获取服务提供者请求地址
        RPCConfig rpcConfig = RPCApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        String serviceName = request.getServiceName();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RPCConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无可用服务提供者");
        }

        // 负载均衡
        HashMap<String, Object> requestParams = new HashMap<>();
        requestParams.put("serviceName", request.getServiceName());
        ServiceMetaInfo metaInfo = loadBalancer.select(requestParams, serviceMetaInfos);
        log.info("address{}", metaInfo.getServiceAddress());
        InetSocketAddress socketAddress = new InetSocketAddress(metaInfo.getServiceHost(), metaInfo.getServicePort());
        log.info("service url:{}", socketAddress);

        // 使用重试策略
        RPCResponse response;
        try {
            // todo 后期可改成从配置文件中读取重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance("fixedinterval");
            response = retryStrategy.doRetry(() -> {
                Channel channel = nettyClient.getChannel(socketAddress);
                if (channel.isActive()) {
                    CompletableFuture<RPCResponse> resultFuture = new CompletableFuture<>();
                    ZMessage message = buildMessage(request);
                    UnprocessedRequests.put(message.getHeader().getRequestId(), resultFuture);
                    channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            log.info("client send message{}", message);
                        } else {
                            future.channel().close();
                            resultFuture.completeExceptionally(future.cause());
                            log.info("send failed{}", future.cause());
                        }
                    });
                    return resultFuture.get();
                } else {
                    throw new RuntimeException("channel is not active. address=" + socketAddress);
                }
            });
        } catch (Exception e) {
              // todo 容错策略
            TolerantStrategy strategy = TolerantStrategyFactory.getInstance("failover");
            Map<String, Object> context = new HashMap<>();
            context.put(TolerantStrategyConstant.SERVICE_LIST, serviceMetaInfos);
            context.put(TolerantStrategyConstant.CURRENT_SERVICE, metaInfo);
            context.put(TolerantStrategyConstant.RPC_REQUEST, request);
            response = strategy.doTolerant(context, e);
        }
        return new FutureResult(CompletableFuture.completedFuture(response));
    }

    public static ZMessage<?> buildMessage(RPCRequest request) {
        ZMessage.Header header = new ZMessage.Header();
        header.setMagic(ProtocolConstant.MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerialize((byte) SerializerEnum.getByValue(RPCApplication.getRpcConfig().getSerializer()).getKey());
        header.setType(MessageType.REQUEST.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setCompress((byte) 0);
        header.setStatus((byte) 0);
        header.setBodyLength(request.toString().getBytes().length);
        return ZMessage.builder().header(header).body(request).build();
    }
}