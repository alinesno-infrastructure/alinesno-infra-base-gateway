package com.alinesno.infra.base.gateway.proxy.gateway.event;

import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.entity.Balanced;
import com.alinesno.infra.base.gateway.facade.entity.LoadServer;
import com.alinesno.infra.base.gateway.facade.entity.Route;
import com.alinesno.infra.base.gateway.domain.dao.RouteDao;
import com.alinesno.infra.base.gateway.domain.service.BalancedService;
import com.alinesno.infra.base.gateway.domain.service.LoadServerService;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import com.alinesno.infra.base.gateway.proxy.gateway.service.LoadRouteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description 注册网关路由事件，动态加载数据库网关路由信息，监听路由配置事件，重新动态加载网关路由数据
 * @author  JL
 * @date 2020/05/27
 * @version 1.0.0
 */
@Slf4j
@Component
public class DataRouteApplicationEventListen implements RouteDefinitionLocator,ApplicationEventPublisherAware {

    @Resource
    private RouteDao routeDao;

    @Resource
    private LoadRouteService loadRouteService;

    @Resource
    private BalancedService balancedService;

    @Resource
    private LoadServerService loadServerService;
    private ApplicationEventPublisher publisher;

    private final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 监听事件刷新配置；
     * 1.CustomApplicationEvent发布后，即触发listenEvent事件方法；
     * 2.publisher.publishEvent方法执行RefreshRoutesEvent事件后；
     * 3.gateway加调用getRouteDefinitions方法重新获取网关路由集合配置并刷新内存
     * （已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
     */
    @Deprecated
    @EventListener(classes = DataRouteApplicationEvent.class)
    public void listenEvent() {
        // Todo 停止使用，请用InitRouteService类init()方法
        //init();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 获取所有网关路由信息
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routeDefinitions);
    }

//    @Deprecated
//    @Override
//    public Mono<Void> save(Mono<RouteDefinition> route) {
//        return Mono.defer(() -> Mono.error(new NotFoundException("此save方法不提供功能实现，请勿调用")));
//    }

//    @Deprecated
//    @Override
//    public Mono<Void> delete(Mono<String> routeId) {
//        return Mono.defer(() -> Mono.error(new NotFoundException("此delete方法不提供功能实现，请勿调用")));
//    }

    @Deprecated
    //@PostConstruct
    public void init(){
        initLoadRoute();
        initLoadBalanced();
    }

    /**
     * 初始化完毕后，加载路由
     */
    public void initLoadRoute(){
        Route query = new Route();
        query.setStatus(Constants.YES);
        //一定要清空routeDefinitions否则每次刷新会往集合中添加重复数据
        routeDefinitions.clear();
        try {
            List<Route> list = routeDao.findAll(Example.of(query));
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(r -> {
                    RouteCache.put(r.getId(), r);
                    routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
                });
            }
            log.info("监听到网关路由配置发生变更，重新加载网关路由配置共{}条", routeDefinitions.size());
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
        List<Route> balancedRouteList = new ArrayList<>();
        try {
            List<Route> list = routeDao.findAll(Example.of(query));
            List<Balanced> balancedList = balancedService.findAll(new Balanced());
            //先将所有负载路由清空
            routeDefinitions.removeIf(route -> route.getId().startsWith(RouteConstants.BALANCED));
            if (!CollectionUtils.isEmpty(balancedList)){
                List<LoadServer> loadServerList = loadServerService.findAll(new LoadServer());
                balancedList.forEach(b->{
                    if (b.getStatus().equals(Constants.YES)){
                        //查找负载下注册的服务
                        List<LoadServer> serverList = loadServerList.stream().filter(l-> l.getBalancedId().equals(b.getId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(serverList)) {
                            serverList.forEach(s -> {
                                //查找服务对应的路由服务
                                Optional<Route> optionalRoute = list.stream().filter(r -> r.getId().equals(s.getRouteId())).findFirst();
                                if (optionalRoute.isPresent()) {
                                    Route route = getRoute(b, s, optionalRoute);
                                    //添加新路由集合中
                                    balancedRouteList.add(route);
                                }
                            });
                        }
                    }
                });
            }
            //将新的路由加载网关路由集合中
            balancedRouteList.forEach(r->{
                RouteCache.put(r.getId(), r);
                //添加新的路由对象
                routeDefinitions.add(loadRouteService.loadRouteDefinition(r));
            });
            log.info("监听到网关负载路由配置发生变更，重新加载网关负载路由配置共{}条", balancedRouteList.size());
        }catch(Exception e){
            log.error("加载数据库中网关负载路由配置异常：",e);
        }
    }

    @NotNull
    private static Route getRoute(Balanced b, LoadServer s, Optional<Route> optionalRoute) {

        String weightName = RouteConstants.BALANCED + "-" + b.getId();
        Route route = optionalRoute.get();

        //获取route，改变参数，构造一个新route对象
        route.setId(weightName + "-" + route.getId());
        route.setPath(RouteConstants.PARENT_PATH + b.getLoadUri());

        //设置负载参数
        route.setWeightName(weightName);
        route.setWeight(s.getWeight());
        route.setStripPrefix(1);

        return route;
    }

}
