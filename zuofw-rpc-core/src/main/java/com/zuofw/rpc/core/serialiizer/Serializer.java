package com.zuofw.rpc.core.serialiizer;

import java.io.IOException;

/**
 * 自定义序列化接口，方便后续扩展
 */

public interface Serializer {
    /*
     * @description: 序列化
     * @author zuofw
     * @date: 2024/9/6 10:28
    * @param obj
    * @return byte[]
     */
    <T> byte[] serialize(T obj) throws IOException;


    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    //<T>是泛型方法的声明，表示这是一个泛型方法，T是泛型参数，表示这个方法是一个泛型方法，T是泛型参数，表示这个方法可以接受任意类型的参数
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
