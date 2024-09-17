package com.zuofw.rpc.loadbalance;


import com.zuofw.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * @description:  负载均衡器接口（消费端使用)
 * @author zuofw
 * @date 2024/9/17 14:40
 * @version 1.0
 */
public interface LoadBalancer {
    /*
     * @description:  选择服务进行调用
     * @author zuofw
     * @date: 2024/9/17 11:05
     * @param params
     * @param serviceMetaInfos
     * @return com.zuofw.rpc.model.ServiceMetaInfo
     */
    ServiceMetaInfo select(Map<String, Object> params, List<ServiceMetaInfo> serviceMetaInfos);
}
