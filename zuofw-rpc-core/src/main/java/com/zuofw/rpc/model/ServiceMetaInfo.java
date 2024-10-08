package com.zuofw.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 〈服务信息〉
 *
 * @author zuofw
 * @create 2024/9/11
 * @since 1.0.0
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务地址
     */
    private String serviceAddress;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";
    private String serviceHost;
    private Integer servicePort;

    /**
     * 获取服务键名
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务节点键名
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s", getServiceKey(), getServiceAddress());
    }

    /**
     * 获取完整的服务地址
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}