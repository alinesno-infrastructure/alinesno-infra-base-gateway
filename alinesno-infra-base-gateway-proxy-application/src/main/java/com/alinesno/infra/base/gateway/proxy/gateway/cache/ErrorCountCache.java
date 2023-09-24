package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suze
 * @data 2023/05/06 15:58
 */
public class ErrorCountCache {
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
