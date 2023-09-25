package com.alinesno.infra.base.gateway.proxy.gateway.filter;

import com.alinesno.infra.base.gateway.formwork.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.formwork.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RegServerCache;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * @description 客户端ID过滤
 * @author  JL
 * @date 2020/05/19
 * @version 1.0.0
 */
@Slf4j
public class ClientIdGatewayFilter implements GatewayFilter, Ordered {

    private String routeId;

    public ClientIdGatewayFilter(String routeId){
        this.routeId = routeId;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //做了负载均衡的route服务不做客户端ID验证
        if (routeId.startsWith(RouteConstants.BALANCED)){
            return chain.filter(exchange);
        }
        String ip = NetworkIpUtils.getIpAddress(request);
        String clientId = this.getClientId(request);
        if (StringUtils.isBlank(clientId)){
            String msg = "客户端ID值为空，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.warn(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        GatewayRegServer regServer = getCacheRegServer(clientId);
        if (regServer == null){
            String msg = "客户端ID未注册使用，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.warn(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 查询和对比缓存中的注册客户端
     * @param clientId
     * @return
     */
    public GatewayRegServer getCacheRegServer(String clientId){
        List<GatewayRegServer> regServers = RegServerCache.get(routeId);
        if (CollectionUtils.isEmpty(regServers)){
            return null;
        }
        Optional<GatewayRegServer> optional = regServers.stream().filter(r -> clientId.equals(r.getClientId())).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * 获取请求头部的clientId值
     * @param request
     * @return
     */
    public String getClientId(ServerHttpRequest request){
        String clientId = request.getQueryParams().getFirst(RouteConstants.CLIENT_ID);
        if (StringUtils.isBlank(clientId)){
            clientId = request.getHeaders().getFirst(RouteConstants.CLIENT_ID);
        }
        return clientId;
    }

    /**
     * 是否允许通行客户端ID
     * @return
     */
//    @Deprecated
//    public boolean isPassClientId(String routeId, String clientId){
//        List<String> ids = (List<String>)ClientIdCache.get(routeId);
//        return ids != null && ids.contains(clientId);
//    }

    @Override
    public int getOrder() {
        return 2;
    }
}
