package com.zuofw.rpc.registry;

import com.zuofw.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 〈〉
 *
 * @author zuofw
 * @create 2024/9/11
 * @since 1.0.0
 */
public class RegistryInstanceCache {

    List<ServiceMetaInfo> serviceCache;

    /*
     * @description:   写缓存
     * @author zuofw
     * @date: 2024/9/11 18:53
     * @param newServiceCache
     */
    void setCache(List<ServiceMetaInfo> newServiceCache) {
        serviceCache = newServiceCache;
    }

    /*
     * @description:  读缓存
     * @author zuofw
     * @date: 2024/9/11 18:53
     * @return java.util.List<com.zuofw.rpc.model.ServiceMetaInfo>
     */
    List<ServiceMetaInfo> getCache() {
        return serviceCache;
    }
    /*
     * @description:  清空缓存
     * @author zuofw
     * @date: 2024/9/11 18:53
     */
    void clearCache() {
        serviceCache = null;
    }
}