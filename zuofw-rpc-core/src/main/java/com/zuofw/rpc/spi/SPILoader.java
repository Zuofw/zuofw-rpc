package com.zuofw.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.zuofw.rpc.serialiizer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈SPI加载工具类,实现单例模式加载〉
 *
 * @author zuowei
 * @create 2024/9/11
 * @since 1.0.0
 */
@Slf4j
public class SPILoader {
    /**
     * 存储已经加载的类
     * 接口名1 : {
     *     key:实现类
     * }
     */
    //Class和Object的区别
    private static Map<String, Map<String, Class<?>>>  loaderMap = new ConcurrentHashMap<>();

    /**
     * 存放对象实例缓存，保证单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCANS_DIRS = new String[] {
            RPC_CUSTOM_SPI_DIR,
            RPC_SYSTEM_SPI_DIR
    };

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);


    /**
     * @description:  加载所有的SPI
     * @author bronya
     * @date 2024/9/11 14:44
     * @version 1.0
     */
    public static void loadAll() {
        log.info("开始加载所有的SPI");
        for(Class<?> c : LOAD_CLASS_LIST) {
            load(c);
        }
    }

    public static Map<String,Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为{} 的SPI",loadClass.getName());
        //扫描路径，用户自定义的SPI优先级高于系统SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for(String dir : SCANS_DIRS) {
            log.info("扫描路径为{}",dir);
            //获取资源
            log.info("资源为{}",dir + loadClass.getName());
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            //URL是什么类型，为什么要用它，因为它是一个统一资源定位符，可以用来定位资源
            //url.openStream()是什么意思，是打开一个输入流，可以用来读取资源
            for(URL resource : resources) {
                try {
                    //打开一个输入流
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    //缓冲读取字符流
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] splits = line.split("=");
                        if(splits.length < 2) {
                            log.error("SPI配置文件格式错误");
                            continue;
                        }
                        String key = splits[0];
                        String className = splits[1];
                        //Class.forNam用于动态加载一个类
                        keyClassMap.put(key, Class.forName(className));

                    }
                } catch (IOException e) {
                    throw new RuntimeException("加载SPI配置文件失败",e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("需要加载的类不存在",e);
                }

            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    /**
     * 获取某个类型的实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */

    public static <T> T getInstance(Class<?> tClass, String key) {
        // 获得类名
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap.isEmpty()) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key= %s", tClassName, key));
        }
        // 通过类名获得实现类
        Class<?> implClass = keyClassMap.get(key);
        // 从缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        // 判断缓存中是否有实例
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName,  implClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(String.format("实例化 %s 失败", implClassName), e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
}