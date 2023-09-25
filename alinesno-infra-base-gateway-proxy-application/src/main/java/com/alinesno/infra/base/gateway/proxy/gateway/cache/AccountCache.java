package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于进行用户权限划分的， key 是用户 token，用户初次添加路由或ip时创建，value 是route或ip
 * @author  suze
 * @data 2023/04/25 14:14
 */
public class AccountCache {

    private static ConcurrentHashMap<String, List<String>> accountRouteCacheMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, List<String>> accountIpCacheMap = new ConcurrentHashMap<>();

    public static void putRouteCache(final String key,final List<String> value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        accountRouteCacheMap.put(key, value);
    }

    public static void putRouteCaches(ConcurrentHashMap<String, List<String>> routeCache){
        Assert.notNull(routeCache, "cache cannot is null");
        accountRouteCacheMap = routeCache;
    }

    public static List getRouteCache(final String key){
        List list = accountRouteCacheMap.get(key);
        if(list == null){
            ArrayList<String> keyRoutes = new ArrayList<>();
            accountRouteCacheMap.put(key, keyRoutes);
            return keyRoutes;
        }
        return list;
    }

    public static List<String> getRouteCacheKeys(){
        List<String> keys = new ArrayList<>();
        Iterator iterator = accountRouteCacheMap.keys().asIterator();
        while (iterator.hasNext()){
            keys.add((String)iterator.next());
        }
        return keys;
    }

    public static synchronized void removeRouteCache(final String key){
        if (accountRouteCacheMap.containsKey(key)){
            accountRouteCacheMap.remove(key);
        }
    }

    public static ConcurrentHashMap<String, List<String>> getRouteCacheAll(){
        return accountRouteCacheMap;
    }


    public static void putIpCache(final String key,final List<String> value){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(value, "hash map value cannot is null");
        accountIpCacheMap.put(key, value);
    }

    public static void putIpCaches(ConcurrentHashMap<String, List<String>> IpCache){
        Assert.notNull(IpCache, "cache cannot is null");
        accountIpCacheMap = IpCache;
    }

    public static List getIpCache(final String key){
        List list = accountIpCacheMap.get(key);
        if(list == null){
            ArrayList<String> keyIps = new ArrayList<>();
            accountIpCacheMap.put(key, keyIps);
            return keyIps;
        }
        return list;
    }

    public static synchronized void removeIpCache(final String key){
        if (accountIpCacheMap.containsKey(key)){
            accountIpCacheMap.remove(key);
        }
    }


    public static synchronized void clearRouteCache(){
        accountRouteCacheMap.clear();
    }

    public static synchronized void clearIpCache(){
        accountIpCacheMap.clear();
    }
}
