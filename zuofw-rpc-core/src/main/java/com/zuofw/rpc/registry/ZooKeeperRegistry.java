package com.zuofw.rpc.registry;

import com.zuofw.rpc.config.RegistryConfig;
import com.zuofw.rpc.model.ServiceMetaInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 〈ZooKeeper实现注册中心〉
 *
 * @author zuowei
 * @create 2024/9/11
 * @since 1.0.0
 */
@Data
@Slf4j
public class ZooKeeperRegistry implements Registry {

    // zk客户端
    private CuratorFramework client;

    // 服务发现
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    // 根节点

    private static final String ZK_ROOT_PATH = "/rpc/zk";

    /**
     * 本地注册节点 key 集合 用于维护续期
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 注册中心缓存
     */
    private final RegistryInstanceCache registryServiceCache = new RegistryInstanceCache();

    /**
     * 监听的key集合, 用于续期
     */
    private final Set<String> watchingKeySet = new HashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        //Client 是 Curator 提供的一个类，用于管理与 Zookeeper 的连接，它提供了一些方法用于创建、删除、读取节点等操作。
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        // 构建serviceDiscovery 实例
        //Discovery 是用于管理服务的注册和发现的组件，它提供了服务注册、服务发现、服务状态监控等功能。
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //注册过去
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));
        log.info("服务注册成功:{}", serviceMetaInfo);

        String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        // 添加节点信息到本地缓存，方便续期
        localRegisterNodeKeySet.add(registerKey);


    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
            String registerKey = ZK_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
            localRegisterNodeKeySet.remove(registerKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存中获取
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceCache.getCache();
        //!= null 和 isEmpty()和 isBlank()的区别, isBlank()是Apache commons-lang3包中的方法，用来判断字符串是否为空或者空格
        //isBlank()方法是对字符串进行处理后再判断是否为空，而isEmpty()方法是直接判断字符串是否为空，不做任何处理。
        if(serviceMetaInfoList != null && !serviceMetaInfoList.isEmpty()){
            return serviceMetaInfoList;
        }
        try {
            // 从zk中获取
            List<ServiceMetaInfo> collect = serviceDiscovery.queryForInstances(serviceKey)
                    .stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            // 缓存
            registryServiceCache.setCache(collect);
            return collect;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前服务销毁");
        try(CuratorFramework client = this.client) {
            for (String key : localRegisterNodeKeySet) {
                try {
                    client.delete().forPath(key);
                } catch (Exception e) {
                    throw new RuntimeException(key + "下线失败", e);
                }
            }
        }
    }

    @Override
    public void heartbeat() {

    }

    @Override
    public void watch(String serviceNodeKey) {
        //监听节点
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        //forDeletes()方法用于监听节点的删除事件，当节点被删除时，会触发监听器的回调方法。
        //forChanges()方法用于监听节点的变化事件，当节点的数据发生变化时，会触发监听器的回调方法。
        if(newWatch) {
            //cache是Curator提供的一个类，用于监听节点的变化，包括节点的增加、删除、数据的变化等。
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener.builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges((oldData, data) -> registryServiceCache.clearCache())
                            .build()
            );
        }
    }


    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //ServiceInstance 是 Curator 提供的一个类，用于描述一个服务实例的信息，包括服务名称、服务地址、服务端口等。
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        return ServiceInstance
                .<ServiceMetaInfo>builder()
                .id(serviceAddress)
                .name(serviceMetaInfo.getServiceKey())
                .address(serviceAddress)
                .port(serviceMetaInfo.getServicePort())
                .payload(serviceMetaInfo)
                .build();

    }
}