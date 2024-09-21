## 个人博客
https://zuofw.github.io/
本项目详细介绍：
https://zuofw.github.io/2024/09/02/xiang-mu-jian-li-shi-xi/shou-xie-rpc/
## 项目介绍
zhi-rpc是一款基于Java、Netty、Zookeeper实现的RPC通信框架，它具有以下核心特性：
1. 使用"微内核+可插拔"架构，通过自定义SPI加载机制，支持缓存，动态替代扩展点组件
2. 灵活使用设计模式来提高系统可扩展性，如单例模式、工厂模式、建造者模式
3. 实现服务调用负载均衡机制，支持轮询、随机、一致性哈希算法，优化调用体验
4. 通过自定义通信协议、支持多种序列化方式，同时实现Gzip压缩，提高网络传输效率。
5. 实现自定义request - response模型，在异步通信条件下确保消息的请求和响应成功
6. 基于自定义starter实现，优化SpringBoot环境下的使用。

## 基本架构
https://zuofw.github.io/2024/09/02/xiang-mu-jian-li-shi-xi/shou-xie-rpc/%E5%9F%BA%E6%9C%AC%E6%9E%B6%E6%9E%84.png
1. 注册中心，用于服务注册和获取
2. 服务端：提供服务的一方Provider
3. 客户端：调用服务的一方Consumer
基本流程：
1. 服务端把服务信息注册到注册中心上，一般包括服务端地址、接口类和方法
2. 客户端从注册中心获取对应的服务信息
3. 客户端根据服务的信息，通过网络调用服务端的接口
## 快速开始
1. SpringBoot环境下引入依赖
```xml
 <dependency>
            <groupId>com.zuofw.rpc.spring.boot.starter</groupId>
            <artifactId>zuofw-rpc-spring-boot-starter</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
```
2. 在启动类上加上 `@EnableZuofwRpc(needServer = false)` 服务提供者将false改为true
```java
@SpringBootApplication  
@EnableZuofwRpc(needServer = false)  
public class ZuofwRpcSpringConsumerApplication {  
  
    public static void main(String[] args) {  
        SpringApplication.run(ZuofwRpcSpringConsumerApplication.class, args);  
    }  
  
}
```
3. 服务提供者在实现类上加上`@ZuofwRPCService`注解
```java
@Service
@ZuofwRPCService
public class UserserviceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("服务端接收到请求，请求参数为：" + user);
        return user;
    }
}
```
4. 服务调用者在需要使用的服务上加上 `@ZuofwRPCReference` 即可使用
```java
   @ZuofwRPCReference
    private UserService userService;

    public void sayHello(String name) {
        User user = new User();
        user.setName("zuofw");
        User resultUser = userService.getUser(user);
        System.out.println("consumer get User:" + resultUser.getName());
    }
```
