package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 缓存路由网关对应的GroovyScript规则引擎脚本ID
 * @Author JL
 * @Date 2022/2/22
 * @Version V1.0
 */
public class RotueGroovyCache {

    private static ConcurrentHashMap<String, List<Long>> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final List<Long> ids){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(ids, "hash map value cannot is null");
        cacheMap.put(key, ids);
    }

    public static synchronized void put(final String key,final long id){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(id, "hash map value cannot is null");
        List<Long> ids = null;
        if (cacheMap.containsKey(key)){
            ids = cacheMap.get(key);
        }
        if (ids == null){
            ids = new ArrayList<>();
        }else if (ids.contains(id)){
            return ;
        }
        ids.add(id);
        cacheMap.put(key, ids);
    }

    public static List<Long> get(final String key){
        return cacheMap.get(key);
    }

    public static synchronized void remove(final String key){
        if (cacheMap.containsKey(key)){
            cacheMap.remove(key);
        }
    }

    public static boolean containsKey(final String key) {
        return cacheMap.containsKey(key);
    }

    public static synchronized void removeValue(final String key, final long id){
        if (cacheMap.containsKey(key)){
            List<Long> ids = cacheMap.get(key);
            if (!CollectionUtils.isEmpty(ids)){
                if (ids.contains(id)){
                    ids.remove(id);
                }
            }
        }
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

    public static ConcurrentHashMap<String,List<Long>> getCacheMap(){
        return cacheMap;
    }

}
