package com.zuofw.rpc.fault.tolerant;

import com.zuofw.rpc.Invoker.NettyInvoker;
import com.zuofw.rpc.Invoker.UnprocessedRequests;
import com.zuofw.rpc.constant.TolerantStrategyConstant;
import com.zuofw.rpc.factory.RetryStrategyFactory;
import com.zuofw.rpc.fault.retry.RetryStrategy;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;
import com.zuofw.rpc.model.ServiceMetaInfo;
import com.zuofw.rpc.model.ZMessage;
import com.zuofw.rpc.server.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 故障转移
 *
 * @author zuowei
 * @create 2024/9/19
 * @since 1.0.0
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{

    @Override
    public RPCResponse doTolerant(Map<String, Object> context, Exception e) {
        List<ServiceMetaInfo> metaInfos = (List<ServiceMetaInfo>) context.get(TolerantStrategyConstant.SERVICE_LIST);
        ServiceMetaInfo metaInfo = (ServiceMetaInfo) context.get(TolerantStrategyConstant.CURRENT_SERVICE);
        RPCRequest rpcRequest = (RPCRequest) context.get(TolerantStrategyConstant.RPC_REQUEST);
        if(metaInfos == null || metaInfos.isEmpty()) {
            log.error("故障转移失败，metaInfos为空");
            return null;
        }
        NettyClient nettyClient = NettyClient.getInstance();
        ZMessage message = NettyInvoker.buildMessage(rpcRequest);
        for(ServiceMetaInfo serviceMetaInfo: metaInfos) {
            if (serviceMetaInfo.equals(metaInfo)) {
                continue;
            }
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance("fixedinterval");
            try {
                return retryStrategy.doRetry(() -> {
                    InetSocketAddress socketAddress = new InetSocketAddress(serviceMetaInfo.getServiceHost(), serviceMetaInfo.getServicePort());
                    Channel channel = nettyClient.getChannel(socketAddress);
                    if (channel.isActive()) {
                        CompletableFuture<RPCResponse> resultFuture = new CompletableFuture<>();
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
            } catch (Exception ex) {
                log.error("故障转移失败，重试失败");
            }
        }
        throw new RuntimeException("容错失败,所有的服务重试都失败");
    }
}