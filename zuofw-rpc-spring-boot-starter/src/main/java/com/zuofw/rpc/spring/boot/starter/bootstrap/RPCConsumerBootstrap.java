package com.zuofw.rpc.spring.boot.starter.bootstrap;

import com.zuofw.rpc.factory.ServiceProxyFactory;
import com.zuofw.rpc.spring.boot.starter.annoation.ZuofwRPCReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author zuofw
 * 服务消费者启动类
 */
@Slf4j
public class RPCConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ZuofwRPCReference rpcReference = field.getAnnotation(ZuofwRPCReference.class);
            if (rpcReference != null) {
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                System.out.println("生成代理对象:" + interfaceClass.getName()+"  "+field.getType());
                field.setAccessible(true);
                log.info("生成代理对象:{}", interfaceClass.getName());
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
//                Object proxy = ServiceProxyFactory.getCGProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    System.out.println("生成代理对象失败");
                    throw new RuntimeException(e);
                }
            }

        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
