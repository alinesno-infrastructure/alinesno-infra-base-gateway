package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

/**
 * @Description 缓存接口请求统计缓存
 * @Author jianglong
 * @Date 2020/07/07
 * @Version V1.0
 */
public class CountCache {

    private static ConcurrentHashMap<String,Integer> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final Integer value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        cacheMap.put(key, value);
    }

    public static Integer get(final String key){
        return cacheMap.get(key);
    }

    public static synchronized void remove(final String key){
        if (cacheMap.containsKey(key)){
            cacheMap.remove(key);
        }
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

    public static ConcurrentHashMap<String,Integer> getCacheMap(){
        return cacheMap;
    }
}
