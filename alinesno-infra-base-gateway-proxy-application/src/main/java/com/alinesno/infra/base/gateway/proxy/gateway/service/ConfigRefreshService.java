package com.alinesno.infra.base.gateway.proxy.gateway.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alinesno.infra.base.gateway.adapter.dto.GatewayConfigDTO;
import com.alinesno.infra.base.gateway.core.entity.*;
import com.alinesno.infra.base.gateway.core.service.*;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RegServerCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import com.alinesno.infra.base.gateway.proxy.gateway.event.ApplicationEventPublisherFactory;
import com.alinesno.infra.base.gateway.proxy.gateway.service.session.BalanceRouteSession;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.alinesno.infra.base.gateway.proxy.gateway.cache.AccountCache.*;

/**
 * @description 为NacosConfigRefreshEventListener监听事件服务提供网关路由更新与删除方法
 * @author  JL
 * @date 2021/09/17
 * @version 1.0.0
 */
@Slf4j
@Service
public class ConfigRefreshService {

    @Resource
    private RouteService routeService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private DynamicRouteService dynamicRouteService;
    @Resource
    private SecureIpService secureIpService;
    @Resource
    private RegServerService regServerService;
    @Resource
    private BalancedService balancedService;
    @Resource
    private LoadServerService loadServerService;
    @Resource
    private ClientService clientService;
    @Resource
    private DynamicGroovyService dynamicGroovyService;
    @Resource
    private ApplicationEventPublisherFactory applicationEventPublisherFactory;
    @Resource
    private BalanceRouteSession balanceRouteSession;

    /**
     * 解析网关路由动态变更配置
     * @param gatewayConfig
     */
    public void setGatewayConfig(GatewayConfigDTO gatewayConfig){
        log.info("获取网关路由配置内容，gateway={}" , gatewayConfig);
        try {
            refreshGatewayConfig(gatewayConfig);
        } catch (Exception e){
            log.error("加载网关路由配置异常！" + e.getMessage() + "\n", e);
        }
    }

    /**
     * 重新设置网关路由配置：balanced,route,client,ip
     * @param gatewayConfig
     */
    public void refreshGatewayConfig(GatewayConfigDTO gatewayConfig){

        String balancedId = gatewayConfig.getBalancedId();
        String routeId = gatewayConfig.getRouteId();
        Long regServerId = gatewayConfig.getRegServerId();
        String clientId = gatewayConfig.getClientId();
        String ip = gatewayConfig.getIp();
        String operatorToken = gatewayConfig.getOperatorToken();
        Long groovyScriptId = gatewayConfig.getGroovyScriptId();

        //刷新配置项
        refreshBalanced(balancedId);
        refreshRoute(routeId);
        refreshRegServer(regServerId);
        refreshClient(clientId);
        refreshIp(operatorToken, ip);
        refreshGroovyScript(groovyScriptId);

        // 刷新路由
        if(StrUtil.isNotEmpty(routeId) || StrUtil.isNotEmpty(balancedId) ||ObjectUtil.isNotEmpty(groovyScriptId)){
            applicationEventPublisherFactory.refreshRoutesEvent();
        }
    }

    /**
     * 重新加载route网关负载均衡路由并刷新网关
     * @param balancedId
     */
    public void refreshBalanced(String balancedId){
        if (StringUtils.isBlank(balancedId)) {
            return ;
        }

        ConcurrentHashMap<String, List<String>> cache = getRouteCacheAll();
        Iterator<String> accountIterator = cache.keys().asIterator();

        Balanced balanced = balancedService.findById(balancedId);

        if (balanced == null){
            dynamicRouteService.deleteBalanced(balancedId);
            log.info("未获取网关负载均衡数据库配置！balancedId={}" , balancedId);
            // 清除 accountRoute 相关数据
            while (accountIterator.hasNext()){
                String token = accountIterator.next();
                List<String> accountRouteIds = getRouteCache(token);
                List<String> newAccountRouteIds = accountRouteIds.stream()
                        .filter(item -> !item.contains(balancedId))
                        .collect(Collectors.toList());
                putRouteCache(token, newAccountRouteIds);
            }
            // 清除route-cache 相应数据

            return;
        }

        // 原有balanceRoute
        List<String> oldBalanceRoutesIds = balanceRouteSession.getOldBalanceRoutes(balancedId);

        List<LoadServer> loadServerList = loadServerService.queryByBalancedId(balancedId);
        if(CollectionUtil.isEmpty(loadServerList)){
            log.info("负载均衡配置的负载路由为空");
            if(CollectionUtil.isNotEmpty(oldBalanceRoutesIds)){
                Iterator<String> iterable = oldBalanceRoutesIds.iterator();
                while (iterable.hasNext()){
                    String rId = iterable.next();
                    dynamicRouteService.delete(rId);
                    RouteCache.remove(rId);
                }
                balanceRouteSession.putBalanceRoutes(balancedId, null);
            }
            return;
        }

        boolean isClose = Constants.NO.equals(balanced.getStatus());
        Route route;
        String balancedRouteId;
        //不管任何操作先发布清除事件，清除分组权重配置
        publisherWeightRemoveEvent(balancedId);
        // 清除已取消负载路由
        List<String> clearRouteIds = new ArrayList<>();
        oldBalanceRoutesIds.stream()
                .filter(routeId -> !loadServerList.stream().map(item -> item.getRouteId()).collect(Collectors.toList()).contains(routeId))
                .reduce(null, (result, routId) -> {
                    clearRouteIds.add(routId);
                    RouteCache.remove(routId);
                    dynamicRouteService.delete(routId);
                    return result;
                });
        List<String> newBalanceRoutesIds = new ArrayList<>();
        balanceRouteSession.putBalanceRoutes(balancedId, newBalanceRoutesIds);

        // 清除accountCache 相关数据
        while (accountIterator.hasNext()){
            String accountCacheKey = accountIterator.next();
            List<String> accountRouteIds = getRouteCache(accountCacheKey);
            accountRouteIds.removeAll(clearRouteIds);
        }

        //重新加载每个负载均衡网关路由
        for (LoadServer loadServer : loadServerList){
            //获取route，改变参数，构造一个负载均衡routeId
            balancedRouteId = loadServerService.setBalancedRouteId(balancedId, loadServer.getRouteId());
            //如果当负载均衡网关已禁用，则删除该负载均衡下所有网关路由
            if (isClose){
                dynamicRouteService.delete(balancedRouteId);
                RouteCache.remove(balancedRouteId);
                continue;
            }
            //如果路由权重为0，则表示无流量调用，则清除负载路由
            if (loadServer.getWeight() <= 0){
                dynamicRouteService.delete(balancedRouteId);
                RouteCache.remove(balancedRouteId);
                continue;
            }
            route = routeService.findById(loadServer.getRouteId());
            // 状态，0是启用，1是禁用
            if (route == null || Constants.NO.equals(route.getStatus())) {
                continue;
            }
            getRouteCache(route.getAccountToken()).add(balancedRouteId);
            newBalanceRoutesIds.add(balancedRouteId);
            loadServerService.setBalancedRoute(balanced, loadServer, route);
            RouteCache.put(balancedRouteId, route);
            dynamicRouteService.update(loadRouteService.loadRouteDefinition(route));
        }
        if (isClose){
            log.info("成功移除网关负载均衡配置缓存路由！balancedId={}", balancedId);
        }else {
            log.info("成功加载网关负载均衡配置路由缓存！balancedId={}", balancedId);
        }
    }

    /**
     * 重新加载route网关路由并刷新网关
     * @param routeId
     */
    public void refreshRoute(String routeId){
        if (StringUtils.isBlank(routeId)) {
            return ;
        }
        Route route = routeService.findById(routeId);
        List<String> accountRoutes = new ArrayList<>();
        // null表示已删除，状态，0是启用，1是禁用
        boolean isClose = false;
        if (route == null) {
            route = new Route();
            route.setId(routeId);
            List<String> clearAccountTokens = getRouteCacheKeys().stream()
                    .filter(key -> {
                        List<String> item = getRouteCache(key);
                        if(item.contains(routeId)){
                            item.remove(routeId);
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            String accountToken = CollectionUtil.isNotEmpty(clearAccountTokens)?clearAccountTokens.get(0):null;
            if(accountToken != null){
                route.setAccountToken(accountToken);
            }
            isClose = true;

        } else {
            if (Constants.NO.equals(route.getStatus())){
                isClose = true;
            }
            accountRoutes = getRouteCache(route.getAccountToken());
            accountRoutes.remove(routeId);
        }

        if (isClose) {
            dynamicRouteService.delete(routeId);
            //从本地缓存中移除
            RouteCache.remove(routeId);
            RegServerCache.remove(routeId);
            log.info("成功移除网关路由缓存配置！routeId={}", routeId);
        }else {
            dynamicRouteService.update(loadRouteService.loadRouteDefinition(route));
            //记录到本地缓存中
            RouteCache.put(routeId, route);
            accountRoutes.add(routeId);
            List<Map<String, Object>> regClientList = regServerService.getByRouteRegClientList(routeId);
            if (CollectionUtils.isEmpty(regClientList)){
                log.warn("未获取注册到网关路由客户端数据库配置！routeId={}", routeId);
            }else {
                setRegRouteClient(regClientList, isClose);
            }
            log.info("成功加载网关路由缓存配置！routeId={}", routeId);
        }
        // 更新负载均衡中关联的网关路由配置；
        refreshBalancedRoute(route, isClose);
    }

    /**
     * 刷新负载均衡下的网关路由
     * @param route
     * @param isClose
     */
    public void refreshBalancedRoute(Route route, boolean isClose){
        String routeId = route.getId();
        List<String> accountRoutes;
        if(StrUtil.isEmpty(route.getAccountToken())){
            accountRoutes = new ArrayList<>();
        }else {
            accountRoutes = getRouteCache(route.getAccountToken());
        }
        // 清除accountToken中route记录
        List<String> removeRouteIds = accountRoutes.stream()
                .filter(item -> !(item.equals(routeId)) && item.contains(routeId))
                .collect(Collectors.toList());
        accountRoutes.removeAll(removeRouteIds);

        //删除
        if (isClose){

            Flux<RouteDefinition> routeDefinitionFlux = dynamicRouteService.getRouteDefinitions();
            Mono<List<RouteDefinition>> routeDefinitionMono = routeDefinitionFlux
                    .filter(r->r.getId().startsWith(RouteConstants.BALANCED))
                    .filter(r->r.getId().endsWith("-" + routeId))
                    .collectList();
            AtomicReference<List<RouteDefinition>> routeDefinitionRef = new AtomicReference<>();
            routeDefinitionMono.subscribe(routeDefinitions -> {
                routeDefinitionRef.set(routeDefinitions);
            }, error -> {
                return;
            });
            List<RouteDefinition> routeDefinitionList = routeDefinitionRef.get();
            if (CollectionUtils.isEmpty(routeDefinitionList)){
                return;
            }
            List<String> balancedList = new ArrayList<>();
            //网关路由已删除，必需将route从已加载的负载均衡列表中全部删除
            for (RouteDefinition routeDefinition: routeDefinitionList){
                String balancedId = routeDefinition.getId()
                        .replaceAll(RouteConstants.BALANCED, "")
                        .replaceAll(routeId, "")
                        .replaceAll("-", "");
                balancedList.add(balancedId);
                dynamicRouteService.delete(routeDefinition.getId());
            }

            // 重新计算负载均衡下的权重
            for (String balancedId : balancedList) {
                //发布事件，清除分组权重配置
                //publisherWeightRemoveEvent(balancedId);
                //重新加载负载均衡网关路由配置，会对权重重新进行计算（已在gateway中存在的负载均衡路由，理论上在数据库里的状态是启用）
                refreshBalanced(balancedId);
            }

            return;
        }
        //启用
        List<LoadServer> loadServerList = loadServerService.queryByRouteId(routeId);
        if (CollectionUtils.isEmpty(loadServerList)){
            return ;
        }
        Balanced balanced;
        for (LoadServer loadServer : loadServerList){
            balanced = balancedService.findById(loadServer.getBalancedId());
            // 状态，0是启用，1是禁用
            if (balanced == null || Constants.NO.equals(balanced.getStatus())){
                continue;
            }
            //向负载列表中增加网关路由
            loadServerService.setBalancedRoute(balanced, loadServer, route);
            accountRoutes.add(route.getId());
            dynamicRouteService.add(loadRouteService.loadRouteDefinition(route));
        }
    }

    /**
     * 重新设置注册到route网关缓存客户端ID，有些网关路由开启了注册客户端过滤（鉴权）
     * @param regServerId
     */
    public void refreshRegServer(Long regServerId){
        if (regServerId == null || regServerId <= 0){
            return ;
        }
        RegServer regServer = regServerService.findById(regServerId);
        Assert.notNull(regServer, getExceptionMessage("未获取注册网关路由客户端服务ID数据库配置！regServerId=", String.valueOf(regServerId)));
        Client client = clientService.findById(regServer.getClientId());
        Assert.notNull(client, getExceptionMessage("未获取注册客户端ID数据库配置！clientId=", regServer.getClientId()));
        //封装配置数据
        List regClientList = new ArrayList<>(1);
        regClientList.add(new Object[]{regServer.getRouteId(), regServer.getClientId(), client.getIp(), regServer.getToken(), regServer.getSecretKey(), regServer.getStatus()});
        // 状态，0是启用，1是禁用
        boolean clientClose = Constants.NO.equals(client.getStatus());
        if (setRegRouteClient(regClientList, clientClose)){
            log.info("成功移除注册到网关路由的客户端服务ID缓存配置！regServerId={}", String.valueOf(regServerId));
        }else {
            log.info("成功加载注册到网关路由客户端服务ID缓存配置！regServerId={}", String.valueOf(regServerId));
        }
    }

    /**
     * 重新设置route网关缓存客户端ID（鉴权）
     * @param clientId
     */
    public void refreshClient(String clientId){
        if (StringUtils.isBlank(clientId)){
            return ;
        }
        Client client = clientService.findById(clientId);
        //null表示已删除
        if (client == null){
            deleteRegRouteClient(clientId);
            log.info("成功移除网关路由客户端ID缓存配置！clientId={}", clientId);
            return ;
        }
        List<Map<String, Object>> regClientList = regServerService.getRegClientList(clientId);
        if (CollectionUtils.isEmpty(regClientList)){
            log.error("未获取注册到网关路由客户端ID数据库配置！clientId={}", clientId);
            return ;
        }
        // 状态，0是启用，1是禁用
        boolean clientClose = Constants.NO.equals(client.getStatus());
        if (setRegRouteClient(regClientList, clientClose)){
            log.info("成功移除网关路由客户端ID缓存配置！clientId={}", clientId);
        }else {
            log.info("成功加载网关路由客户端ID缓存配置！clientId={}", clientId);
        }
    }

    /**
     * 重新设置route网关缓存IP（黑|白名单）
     * @param ip
     */
    public void refreshIp(String operatorToken, String ip){
        if (StringUtils.isBlank(operatorToken) || StringUtils.isBlank(ip)) {
            return ;
        }
        SecureIp qSecureIp = new SecureIp();
        qSecureIp.setIp(ip);
        qSecureIp.setAccountToken(operatorToken);
        List<SecureIp> secureIps = secureIpService.findAll(qSecureIp);
        getIpCache(operatorToken).remove(ip);
        // 状态，0是启用，1是禁用
        if (CollectionUtils.isEmpty(secureIps) || Constants.NO.equals(secureIps.get(0).getStatus())){
            getIpCache(operatorToken).add(ip);
            log.info("成功加载网关路由IP名单中缓存配置！ip={}", ip);
        }else {
            log.info("成功移除网关路由IP名单中缓存配置！ip={}", ip);
        }
    }

    /**
     * 重新设置route网关缓存groovyScript规则引擎动态脚本
     * @param groovyScriptId
     */
    public void refreshGroovyScript(Long groovyScriptId){
        if (groovyScriptId == null || groovyScriptId <= 0){
            return ;
        }
        dynamicGroovyService.refresh(groovyScriptId);
        log.info("成功刷新网关路由groovyScript规则引擎动态脚本缓存配置！groovyScriptId={}", groovyScriptId);
    }

    /**
     * 删除缓存中注册客户端ID和客户端IP
     * @param clientId
     */
    private void deleteRegRouteClient(String clientId){
        // 添加或移除注册的客户端
        ConcurrentHashMap<String,List<GatewayRegServer>> cacheMap = RegServerCache.getCacheMap();
        if (CollectionUtils.isEmpty(cacheMap)) {
            return ;
        }
        for (Map.Entry<String, List<GatewayRegServer>> entry : cacheMap.entrySet()) {
            // 移除客户端，返回新集合
            List<GatewayRegServer> newRegServers = entry.getValue().stream().filter(r -> !clientId.equals(r.getClientId())).collect(Collectors.toList());
            entry.setValue(newRegServers);
        }
    }

    /**
     * 设置网关路由中的客户端鉴权信息
     * @param regClientList
     * @param clientClose
     */
    private boolean setRegRouteClient(List regClientList, boolean clientClose){
        String routeId;
        String secretKey;
        String status;
        List<GatewayRegServer> regServers;
        Object[] objects;
        boolean isClose = clientClose;
        Iterator iterator = regClientList.iterator();
        while (iterator.hasNext()){
            objects = (Object[]) iterator.next();
            routeId = String.valueOf(objects[0]);
            String clientId = String.valueOf(objects[1]);
            String ip = String.valueOf(objects[2]);
            String token = String.valueOf(objects[3]);
            secretKey = String.valueOf(objects[4]);
            status = String.valueOf(objects[5]);

            // 如果客户端状态或注册到当前网关路由的访问状态已禁用，则都不可以访问网关路由，需要删除允许通行鉴权配置
            isClose = clientClose || Constants.NO.equals(status);

            // 添加或移除注册的客户端
            regServers = RegServerCache.get(routeId);
            if (CollectionUtils.isEmpty(regServers)){
                regServers = new ArrayList<>();
            }else {
                List<GatewayRegServer> newRegServers = regServers.stream().filter(r -> !clientId.equals(r.getClientId())).collect(Collectors.toList());
                regServers.clear();
                regServers.addAll(newRegServers);
            }
            if (!isClose) {
                GatewayRegServer regServer = new GatewayRegServer();
                regServer.setClientId(clientId);
                regServer.setIp(ip);
                regServer.setToken(token);
                regServer.setSecretKey(secretKey);
                regServers.add(regServer);
            }
            RegServerCache.put(routeId, regServers);
        }
        return isClose;
    }

    /**
     * 抛出异常信息
     * @param message
     * @param id
     */
    private String getExceptionMessage(String message, String id){
        return String.format("%s=%s", message, id);
    }

    /**
     * 发布事件，清除分组权重配置
     * @param balancedId
     */
    private void publisherWeightRemoveEvent(String balancedId){
        applicationEventPublisherFactory.publisherWeightRemoveEvent(loadServerService.setBalancedWeightName(balancedId));
    }

}
