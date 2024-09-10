package com.zuofw.rpc.core.serialiizer;

import java.io.*;

/**
 * 〈JDK序列化实现〉
 *
 * @author zuowei
 * @create 2024/9/6
 * @since 1.0.0
 */
public class JDKSerializer implements Serializer {

    /*
     * @description:   JDK实现
     * @author bronya
     * @date: 2024/9/6 11:06
     * @param obj
     * @return byte[]
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        //这是一个字节数组输出流，数据会被写到一个字节数组中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //将对象写入到字节数组输出流中
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } finally {
            objectInputStream.close();
        }
    }
}