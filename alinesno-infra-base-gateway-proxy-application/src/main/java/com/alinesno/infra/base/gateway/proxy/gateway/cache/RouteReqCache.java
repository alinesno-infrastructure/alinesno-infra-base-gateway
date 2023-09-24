package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

/**
 * @Description 缓存服务路由最后请求时间
 * @Author JL
 * @Date 2021/04/15
 * @Version V1.0
 */
public class RouteReqCache {

    private static ConcurrentHashMap<String,Long> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final Long value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        cacheMap.put(key, value);
    }

    public static Long get(final String key){
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

    public static ConcurrentHashMap<String,Long> getCacheMap(){
        return cacheMap;
    }
}
