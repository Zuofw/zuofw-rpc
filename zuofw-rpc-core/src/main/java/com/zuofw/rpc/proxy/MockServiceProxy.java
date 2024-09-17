package com.zuofw.rpc.proxy;

import com.github.jsonzou.jmockdata.JMockData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 〈Mock服务代理JDK动态代理〉
 *
 * @author zuofw
 * @create 2024/9/10
 * @since 1.0.0
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 返回默认值
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke{}",method.getName());
        Object mockData = JMockData.mock(returnType);
        log.info("mock result:{}",mockData);
        return mockData;
    }

//    /**
//     * 获取默认值
//     *
//     * @param type
//     * @return
//     */
//    private Object getDefaultValue(Class<?> type) {
//        if (type.isPrimitive()) {
//            if (type == boolean.class) {
//                return false;
//            } else if (type == byte.class) {
//                return (byte) 0;
//            } else if (type == short.class) {
//                return (short) 0;
//            } else if (type == int.class) {
//                return 0;
//            } else if (type == long.class) {
//                return 0L;
//            } else if (type == float.class) {
//                return 0.0F;
//            } else if (type == double.class) {
//                return 0.0D;
//            } else {
//                return null;
//            }
//        }
//        // 对象类型返回null
//        return null;
//    }
}