package com.zuofw.rpc.spring.boot.starter.annoation;

import com.zuofw.rpc.spring.boot.starter.bootstrap.RPCConsumerBootstrap;
import com.zuofw.rpc.spring.boot.starter.bootstrap.RPCInitBootStrap;
import com.zuofw.rpc.spring.boot.starter.bootstrap.RPCProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RPCInitBootStrap.class, RPCProviderBootstrap.class, RPCConsumerBootstrap.class})
public @interface EnableZuofwRpc {

    /**
     * 需要启动server
     *
     * @return
     */
    boolean needServer() default true;
}
