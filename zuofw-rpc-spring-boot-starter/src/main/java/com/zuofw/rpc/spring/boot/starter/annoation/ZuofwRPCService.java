package com.zuofw.rpc.spring.boot.starter.annoation;

import com.zuofw.rpc.constant.RPCConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ZuofwRPCService {

    /**
     * 服务接口类
     *
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     *
     * @return
     */
    String serviceVersion() default RPCConstant.DEFAULT_SERVICE_VERSION;
}
