package com.zuofw.rpc.serialiizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuofw.rpc.model.RPCRequest;
import com.zuofw.rpc.model.RPCResponse;

import java.io.IOException;

/**
 * 〈JSON序列化〉
 *
 * @author zuowei
 * @create 2024/9/6
 * @since 1.0.0
 */
public class JSONSerializer implements Serializer{

    //全局唯一
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        //将对象转换为字节数组
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, clazz);
        if(obj instanceof RPCRequest) {
            return handleRequest((RPCRequest) obj, clazz);
        } else if(obj instanceof RPCResponse){
            return  handleResponse((RPCResponse) obj,clazz);
        }
        return obj;
    }

    public <T> T handleResponse(RPCResponse rpcResponse, Class<T> type) throws IOException {
        //为什么要将data转换为字节数组，再转换为对象？
        //因为data是一个Object类型，我们不知道它的具体类型，所以我们需要将它转换为字节数组，再转换为具体的对象
        //将data转换为字节数组
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        //将字节数组转换为对象
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes, rpcResponse.getDataType()));
        // 通过反射创建对象
        return type.cast(rpcResponse);
    }

    public <T> T handleRequest(RPCRequest rpcRequest, Class<T> type) throws IOException {
        // 获取参数类型
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        // 获取参数
        Object[] args = rpcRequest.getArgs();

        // 循环处理每个参数的类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，需要重新转换
            //isAssignableFrom()方法是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的超类或接口
            if (!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(bytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }
}