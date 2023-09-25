package com.alinesno.infra.base.gateway.proxy.gateway.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * @description 缓存客户端Token信息
 *
 * @author  jianglong
 * @author  luoxiaodong
 *
 * @date 2020/05/28
 * @version 1.0.0
 */
public class RegServerCache {
    @Getter
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
        cacheMap.remove(key);
    }

    public static synchronized void clear(){
        cacheMap.clear();
    }

}
