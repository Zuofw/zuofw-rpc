package com.zuofw.rpc.serialiizer;

import com.zuofw.rpc.spi.SPILoader;

/**
 * 〈Serializer工厂〉
 *
 * @author zuofw
 * @create 2024/9/11
 * @since 1.0.0
 */
public class SerializerFactory {
    static {
        /// 加载SPI,类型是Serializer
        SPILoader.load(Serializer.class);
    }

    //默认序列化方式
    private static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    /*
     * @description: 获取序列化方方式
     * @author zuofw
     * @date: 2024/9/11 15:21
     * @param key 值为SPI配置文件中的key
     * @return com.zuofw.rpc.serialiizer.Serializer.Serializer.Serializer
     */
    public static Serializer getInstance(String key) {
        return SPILoader.getInstance(Serializer.class, key);
    }
}