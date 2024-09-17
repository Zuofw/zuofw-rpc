package com.zuofw.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求定义类
 *
 * @author zuofw
 * @create 2024/9/5
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCRequst implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     * Class是Java反射机制中的类，用于描述类的类型信息
     * 为何使用Class<?>而不是Class<T>？
     * Class<?>表示未知类型，而Class<T>表示具体类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;
}