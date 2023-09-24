package com.alinesno.infra.base.gateway.proxy.gateway.component;

import com.alinesno.infra.base.gateway.formwork.base.BaseGroovyService;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 本地缓存GroovyScript规则引擎动态脚本实例化对象
 * @Author JL
 * @Date 2022/2/11
 * @Version V1.0
 */
public class GroovyCache {

    private static final Map<Long, GroovyServiceData> GROOVY_SERVICE_CACHE_MAP = new ConcurrentHashMap<Long, GroovyServiceData>();

    public static synchronized void putGroovyServiceData(long id, String routeId, String name, String extendInfo, String event, BaseGroovyService service, String md5){
        GROOVY_SERVICE_CACHE_MAP.put(id, new GroovyServiceData(routeId, name, extendInfo, event, service, md5));
    }

    public static GroovyServiceData getGroovyServiceData(long id){
        return GROOVY_SERVICE_CACHE_MAP.get(id);
    }

    public static synchronized boolean containsKey(long id){
        return GROOVY_SERVICE_CACHE_MAP.containsKey(id);
    }

    public static synchronized void removeGroovyServiceData(long id){
        if (containsKey(id)) {
            GROOVY_SERVICE_CACHE_MAP.remove(id);
        }
    }

    @Data
    public static class GroovyServiceData {
        private String extendInfo;
        private String name;
        private String routeId;
        private String md5;
        private String event;
        private BaseGroovyService baseGroovyService;

        public GroovyServiceData(String routeId, String name, String extendInfo, String event, BaseGroovyService baseGroovyService, String md5){
            this.routeId = routeId;
            this.name = name;
            this.extendInfo = extendInfo;
            this.event = event;
            this.baseGroovyService = baseGroovyService;
            this.md5 = md5;
        }
    }
}
