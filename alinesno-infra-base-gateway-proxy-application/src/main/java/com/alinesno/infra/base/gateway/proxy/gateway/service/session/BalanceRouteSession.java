package com.alinesno.infra.base.gateway.proxy.gateway.service.session;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Component
public class BalanceRouteSession {
    private final Map<String, List<String>> oldBalanceRouteMap ;

    BalanceRouteSession(){
        oldBalanceRouteMap = new ConcurrentHashMap<>();
    }

    public List<String> getOldBalanceRoutes(String balanceId){
        List<String> result = oldBalanceRouteMap.get(balanceId);
        return result!=null?result:new ArrayList<>();
    }

    public void putBalanceRoutes(String balanceId, List<String> balanceRoutes){
        oldBalanceRouteMap.put(balanceId, balanceRoutes);
    }

    public void removeBalanceRoutes(String balanceId, String balanceRouteId){
        List<String> oldBalanceRoutes = this.getOldBalanceRoutes(balanceId);
        oldBalanceRoutes.remove(balanceRouteId);
    }

}
