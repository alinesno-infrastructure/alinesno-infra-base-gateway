package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @description 缓存路由网关对应的GroovyScript规则引擎脚本ID
 *
 * @author  jianglong
 * @author  luoxiaodong
 *
 * @date 2020/05/28
 * @version 1.0.0
 */
public class RouteGroovyCache {

    @Getter
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
        cacheMap.remove(key);
    }

    public static boolean containsKey(final String key) {
        return cacheMap.containsKey(key);
    }

    public static synchronized void removeValue(final String key, final long id){
        if (cacheMap.containsKey(key)){
            List<Long> ids = cacheMap.get(key);
            if (!CollectionUtils.isEmpty(ids)){
                ids.remove(id);
            }
        }
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

}
