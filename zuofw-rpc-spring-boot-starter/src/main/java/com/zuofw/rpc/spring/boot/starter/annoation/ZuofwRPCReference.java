package com.zuofw.rpc.spring.boot.starter.annoation;


import com.zuofw.rpc.constant.RPCConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZuofwRPCReference {

    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务版本
     * @return
     */
    String serviceVersion() default RPCConstant.DEFAULT_SERVICE_VERSION;
//
//    /**
//     * 负载均衡策略
//     * @return
//     */
//    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;
//
//    /**
//     * 重试策略
//     * @return
//     */
//    String retryStrategy() default RetryStrategyKeys.NO;
//
//    /**
//     * 容错策略
//     * @return
//     */
//    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;
//
//    /**
//     * 是否mock
//     * @return
//     */
//    boolean mock() default false;
}
