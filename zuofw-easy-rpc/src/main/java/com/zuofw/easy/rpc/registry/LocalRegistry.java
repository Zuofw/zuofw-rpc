package com.zuofw.easy.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈本地注册中心实现〉
 *
 * @author zuowei
 * @create 2024/9/6
 * @since 1.0.0
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */

    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /*
     * @description:  注册实现
     * @author bronya
     * @date: 2024/9/6 16:19
     * @param service
     * @param implClass
     */
    public static void register(String service, Class<?> implClass) {
       map.put(service, implClass);
    }
    /*
     * @description:  获取服务
     * @author bronya
     * @date: 2024/9/6 16:19
     * @param service
     * @return java.lang.Class<?>
     */
    public static Class<?> get(String service) {
        //Class是用来
        return map.get(service);
    }

    /**
     * 删除服务
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}