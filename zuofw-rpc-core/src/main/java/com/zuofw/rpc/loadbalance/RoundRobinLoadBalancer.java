package com.zuofw.rpc.loadbalance;

import com.zuofw.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈轮询负载均衡器〉
 *
 * @author zuofw
 * @create 2024/9/17
 * @since 1.0.0
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    // 保证线程安全
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public ServiceMetaInfo select(Map<String, Object> params, List<ServiceMetaInfo> serviceMetaInfos) {
        if(serviceMetaInfos.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfos.size();
        if(size == 0) {
            return serviceMetaInfos.get(0);
        }
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfos.get(index);
    }
}