package com.alinesno.infra.base.gateway.proxy.gateway.service.load;

import com.alinesno.infra.base.gateway.formwork.dao.RouteDao;
import com.alinesno.infra.base.gateway.formwork.entity.Balanced;
import com.alinesno.infra.base.gateway.formwork.entity.LoadServer;
import com.alinesno.infra.base.gateway.formwork.entity.Route;
import com.alinesno.infra.base.gateway.formwork.service.BalancedService;
import com.alinesno.infra.base.gateway.formwork.service.LoadServerService;
import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.AccountCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import com.alinesno.infra.base.gateway.proxy.gateway.service.DynamicRouteService;
import com.alinesno.infra.base.gateway.proxy.gateway.service.LoadRouteService;
import com.alinesno.infra.base.gateway.proxy.gateway.service.session.BalanceRouteSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description 初始化负载均衡配置、网关路由配置
 * @author  JL
 * @date 2021/09/22
 * @version 1.0.0
 */
@Slf4j
@Service
public class InitRouteService {

    private List<RouteDefinition> routeDefinitions = new ArrayList<>();

    private List<String> cacheRouteIds = new ArrayList<>();

    @Resource
    private RouteDao routeDao;
    @Resource
    private BalancedService balancedService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private LoadServerService loadServerService;
    @Resource
    private DynamicRouteService dynamicRouteService;
    @Resource
    private BalanceRouteSession balanceRouteSession;


    /**
     * 初始化执行
     */
    @PostConstruct
    public void init(){
        //一定要清空routeDefinitions否则每次刷新会往集合中添加重复数据
        routeDefinitions.clear();
        initLoadRoute();
        initLoadBalanced();
        initAccountRouteCache();
        dynamicRouteService.addAll(routeDefinitions);
    }

    /**
     * 初始化完毕后，加载路由
     */
    public void initLoadRoute(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        try {
            List<Route> routeList = routeDao.findAll(Example.of(query));
            if (CollectionUtils.isEmpty(routeList)) {
                log.warn("初始化网关路由，无可用网关路由配置...");
                return ;
            }
            routeList.forEach(r -> {
                RouteCache.put(r.getId(), r);
                routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
            });
            log.info("初始化加载网关路由配置共{}条", routeList.size());

            cacheRouteIds.addAll(routeList.stream().map(item -> item.getId()).collect(Collectors.toList()));

        }catch(Exception e){
            log.error("加载数据库中网关路由配置异常：",e);
        }
    }

    /**
     * 初始化完毕后，加载负载路由
     */
    public void initLoadBalanced(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        Balanced balanced = new Balanced();
        balanced.setStatus(Constants.YES);
        List<Route> balancedRouteList = new ArrayList<>();
        List<Balanced> balancedList = balancedService.findAll(balanced);
        if (CollectionUtils.isEmpty(balancedList)) {
            log.info("初始化网关负载均衡路由，无可用配置...");
            return ;
        }
        //查找负载均衡关联的网关路由列表
        List<LoadServer> loadServerList = loadServerService.findAll(new LoadServer());
        if (CollectionUtils.isEmpty(loadServerList)) {
            log.error("初始化网关负载均衡路由，未添加网关路由配置...");
            return ;
        }
        //查询所有可用的网关路由列表
        List<Route> routeList = routeDao.findAll(Example.of(query));
        if (CollectionUtils.isEmpty(routeList)){
            log.error("初始化网关负载均衡路由，无可用网关路由配置...");
            return ;
        }
        Map<String, Route> routeMap =  routeList.stream().collect(Collectors.toMap(Route::getId, r->r));
        // 将同一个负载下的网关路由放在同一个集合中(注意：过滤掉权重值为0的网关，表示无流量流入，无需创建网关)
        Map<String, List<LoadServer>> serverRouteMap = loadServerList.stream().filter(s->s.getWeight()>0)
                .collect(Collectors.toMap(LoadServer::getBalancedId,
                    l-> new ArrayList<LoadServer>(){
                        {add(l);}
                    },
                    (List<LoadServer> v1, List<LoadServer> v2) ->{
                        v1.addAll(v2);
                        return v1;
                    })
                );
        // 添加session
        for (Map.Entry<String, List<LoadServer>> entry : serverRouteMap.entrySet()){
            balanceRouteSession.putBalanceRoutes(entry.getKey(),entry.getValue().stream()
                    .map(item -> item.getRouteId()).collect(Collectors.toList()));
        }
        try {
            // 遍历负载均衡配置，组装生成新的route结构，并加载到gateway网关路由集合中
            for (Balanced b : balancedList){
                //查找负载下注册的服务
                List<LoadServer> serverList = serverRouteMap.get(b.getId());
                if (CollectionUtils.isEmpty(serverList)) {
                    continue;
                }
                serverList.forEach(s -> {
                    Route route = routeMap.get(s.getRouteId());
                    if (route != null) {
                        Route bRoute = new Route();
                        BeanUtils.copyProperties(route, bRoute);
                        loadServerService.setBalancedRoute(b, s, bRoute);
                        //添加新路由集合中
                        balancedRouteList.add(bRoute);
                    }
                });
            }
            //将新的路由加载网关路由集合中
            balancedRouteList.forEach(r->{
                //记录到本地缓存中
                RouteCache.put(r.getId(), r);
                //添加新的负载均衡路由对象
                routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
            });

            cacheRouteIds.addAll(balancedRouteList.stream().map(item -> item.getId()).collect(Collectors.toList()));

        }catch(Exception e){
            log.error("加载数据库中网关负载路由配置异常：",e);
        }
        log.info("初始化加载网关负载路由配置共{}条", balancedRouteList.size());
    }

    private void initAccountRouteCache(){
//        ConcurrentHashMap<String, List<String>> routeCache =  cacheRouteIds.stream()
        ConcurrentHashMap<String, List<String>> routeCache =  cacheRouteIds.stream()
                .map(routeId -> (Route)RouteCache.get(routeId))
                .filter(item -> item.getAccountToken() != null)
                .collect(Collectors.toMap(
                route -> route.getAccountToken(),
                r-> new ArrayList<String>(){
                    {add(r.getId());}
                },
                (List<String> v1, List<String> v2) ->{
                    v1.addAll(v2);
                    return v1;
                },
                ConcurrentHashMap::new)
        );

        AccountCache.putRouteCaches(routeCache);
    }

}
