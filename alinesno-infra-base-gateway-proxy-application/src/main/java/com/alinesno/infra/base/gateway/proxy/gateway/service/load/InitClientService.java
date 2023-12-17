package com.alinesno.infra.base.gateway.proxy.gateway.service.load;

import com.alinesno.infra.base.gateway.core.service.RegServerService;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RegServerCache;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @description  初始化网关路由客户端配置
 * @author  JL
 * @date 2021/09/22
 * @version 1.0.0
 */
@Slf4j
@Service
public class InitClientService {

    @Resource
    private RegServerService regServerService;

    /**
     * 第一次初始化加载
     */
    @PostConstruct
    public void initLoadClient(){
        List list = regServerService.allRegClientList();
        RegServerCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            Iterator iterator = list.iterator();
            String routeId ;
            String ip;
            String id;
            String token;
            String secretKey;
            Object [] object;
            List<GatewayRegServer> regServers ;
            while (iterator.hasNext()){
                object = (Object[]) iterator.next();
                routeId = String.valueOf(object[0]);
                //添加网关路由注册的客户端ID
                id = String.valueOf(object[1]);
                //添加网关路由注册的客户端IP
                ip = String.valueOf(object[2]);
                //添加网关路由注册的客户端token
                token = String.valueOf(object[3]);
                //添加网关路由注册的客户端密钥（用于jwtToken解析）
                secretKey = String.valueOf(object[4]);
                //添加到缓存中
                GatewayRegServer regServer = new GatewayRegServer();
                regServer.setClientId(id);
                regServer.setIp(ip);
                regServer.setToken(token);
                regServer.setSecretKey(secretKey);
                regServers = RegServerCache.get(routeId);
                if(regServers == null) {
                    regServers = new ArrayList<>();
                }
                regServers.add(regServer);
                RegServerCache.put(routeId, regServers);
            }
        }
        log.info("初始化加载客户端配置共{}条", size);
    }

}
