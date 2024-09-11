package com.zuofw.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 加载配置
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置 带环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        // 如果有环境，加载对应环境的配置
        if (StrUtil.isNotBlank(environment)) {
            //  application-dev.properties
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        //读取yaml配置文件
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix);
    }
}
