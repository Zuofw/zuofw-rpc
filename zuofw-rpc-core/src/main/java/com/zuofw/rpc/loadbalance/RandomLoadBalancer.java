package com.zuofw.rpc.loadbalance;

import com.zuofw.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 〈随机balance〉
 *
 * @author zuofw
 * @create 2024/9/17
 * @since 1.0.0
 */
public class RandomLoadBalancer implements LoadBalancer{

    private final Random random = new Random();
    @Override
    public ServiceMetaInfo select(Map<String, Object> params, List<ServiceMetaInfo> serviceMetaInfos) {
        int size = serviceMetaInfos.size();
        if(size == 0) {
            return null;
        }
        if(size == 1) {
            return serviceMetaInfos.get(0);
        }
        return serviceMetaInfos.get(random.nextInt(size));
    }
}