package com.zuofw.rpc.factory;

import com.zuofw.rpc.compressor.Compressor;
import com.zuofw.rpc.spi.SPILoader;

/**
 * compressor factory
 *
 * @author zuofw
 * @create 2024/9/19
 * @since 1.0.0
 */
public class CompressorFactory {
   static {
       SPILoader.load(Compressor.class);
   }
   public static Compressor getInstance(String key) {
       return SPILoader.getInstance(Compressor.class, key);
   }
}