package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 缓存客户端信息
 *
 * @author  jianglong
 * @author  luoxiaodong
 *
 * @date 2020/05/28
 * @version 1.0.0
 */
public class ClientIdCache {

    @Getter
    private static ConcurrentHashMap<String,Object> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final Object value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        cacheMap.put(key, value);
    }

    public static Object get(final String key){
        return cacheMap.get(key);
    }

    public static synchronized void remove(final String key){
        cacheMap.remove(key);
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

}
