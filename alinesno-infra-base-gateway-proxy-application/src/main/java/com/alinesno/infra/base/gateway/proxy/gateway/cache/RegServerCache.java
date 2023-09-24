package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import org.springframework.util.Assert;

/**
 * @Description 缓存客户端Token信息
 * @Author JL
 * @Date 2021/09/28
 * @Version V1.0
 */
public class RegServerCache {
    private static ConcurrentHashMap<String,List<GatewayRegServer>> cacheMap = new ConcurrentHashMap<>();

    public static void put(final String key,final List<GatewayRegServer> regServers){
        Assert.notNull(key, "hash map key cannot is null");
        Assert.notNull(regServers, "hash map value cannot is null");
        cacheMap.put(key, regServers);
    }

    public static List<GatewayRegServer> get(final String key){
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

    public static ConcurrentHashMap<String,List<GatewayRegServer>> getCacheMap(){
        return cacheMap;
    }
}
