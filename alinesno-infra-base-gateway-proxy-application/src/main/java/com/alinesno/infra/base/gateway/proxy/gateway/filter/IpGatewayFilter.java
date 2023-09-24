package com.alinesno.infra.base.gateway.proxy.gateway.filter;

import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.formwork.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.formwork.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.AccountCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RegServerCache;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRegServer;
import lombok.extern.slf4j.Slf4j;
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
 * @Description IP过滤
 * @Author JL
 * @Date 2020/05/19
 * @Version V1.0
 */
@Slf4j
public class IpGatewayFilter implements GatewayFilter, Ordered {

    private String routeId;
    public IpGatewayFilter(String routeId){
        this.routeId = routeId;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //做了负载均衡的route服务不做客户端IP验证
        if (routeId.startsWith(RouteConstants.BALANCED)){
            return chain.filter(exchange);
        }
        String ip = NetworkIpUtils.getIpAddress(request);
        if (!this.isPassIp(ip, exchange.getRequest().getHeaders().get(Constants.AUTH_GATEWAY).get(0))){
            String msg = "客户端IP已被限制，无权限访问网关!" +" ip:" + ip;
            log.warn(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        GatewayRegServer regServer = getCacheRegServer(ip);
        if (regServer == null){
            String msg = "客户端IP未注册使用，无权限访问网关路由："+ routeId +"! Ip:" + ip;
            log.warn(msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 查询和对比缓存中的注册客户端
     * @param ip
     * @return
     */
    public GatewayRegServer getCacheRegServer(String ip){
        List<GatewayRegServer> regServers = RegServerCache.get(routeId);
        if (CollectionUtils.isEmpty(regServers)){
            return null;
        }
        Optional<GatewayRegServer> optional = regServers.stream().filter(r -> ip.equals(r.getIp())).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * 是否允许通行IP
     * @return
     */
    private boolean isPassIp(String ip, String authToken){

        List<String> ips = AccountCache.getIpCache(authToken);
        if(CollectionUtils.isEmpty(ips)){
            return true;
        }

        return !ips.contains(ip);
    }

    /**
     * 是否注册通行IP
     * @return
     */
//    @Deprecated
//    private boolean regPassIp(String routeId,String ip){
//        List<String> ips = (List<String>)RegIpListCache.get(routeId);
//        return ips != null && ips.contains(ip);
//    }

    @Override
    public int getOrder() {
        return 1;
    }
}
