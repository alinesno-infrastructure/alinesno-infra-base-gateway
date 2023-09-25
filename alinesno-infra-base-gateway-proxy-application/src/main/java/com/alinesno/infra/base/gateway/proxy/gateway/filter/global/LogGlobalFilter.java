package com.alinesno.infra.base.gateway.proxy.gateway.filter.global;

import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.CountCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.ErrorCountCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteReqCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @description 日志全局过滤器
 * @author  jianglong
 * @date 2020/06/01
 * @version 1.0.0
 */
@Slf4j
@Component
public class LogGlobalFilter implements GlobalFilter, Ordered {

    private final static String  PLACEHOLDER = "-";
    private final static String LOG_FORMAT = "{} {} {} [{}] \"{} {} HTTP/1.1\" {} {} {} \"{}\" \"{}\" \"{}\" {}";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Date requestTime = new Date();
        Mono<Void> completeMono = chain.filter(exchange);
        /**
         * doFinally(),执行后置过滤方法，只有响应返回后才执行
         */
        return completeMono.doFinally(signal -> {
            ServerHttpRequest request = exchange.getRequest();
            Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
            String routeId = route.getId();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String remoteAddr = NetworkIpUtils.getIpAddress(request);
            String method = request.getMethod().name();
            HttpStatusCode httpStatus = response.getStatusCode();
            int status = (httpStatus == null) ? 500 : httpStatus.value();
            long length = headers.getContentLength();
            URI uri = request.getURI();
            String host = uri.getHost();
            String path = uri.getPath();
            String uriStr = uri.toString();
            String userInfo = uri.getUserInfo();
            userInfo = (userInfo == null) ? PLACEHOLDER : userInfo;
            List<String> userAgentList = headers.get("User-Agent");
            String userAgent = (userAgentList == null) ? PLACEHOLDER : userAgentList.toString();
            MediaType mediaType = headers.getContentType();
            String contentType = (mediaType == null) ? PLACEHOLDER : mediaType.toString();
            long useOfTime = System.currentTimeMillis() - requestTime.getTime();

            //创建自定义请求监控日志
            //114.67.85.24 - - [03/Dec/2019:23:53:06 +0800] "GET /index.php HTTP/1.1" 200 0 114.67.101.85 "http://114.67.101.85:80/index.php" "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1)" "0.000"
            log.trace(LOG_FORMAT, remoteAddr, userInfo, routeId, requestTime, method, path, host, status, length, uriStr, userAgent, contentType, useOfTime);
            //增加统计
            count(routeId, status);
            //记录请求时间
            timeLog(routeId);
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 累加网关服务请求数
     * @param routeId
     */
    public void count(String routeId, int status){
        Integer count = CountCache.get(routeId);
        if(status >= 400) {
            ErrorCountCache.put(routeId, Optional.ofNullable(ErrorCountCache.get(routeId)).orElse(0) + 1);
        }
        CountCache.put(routeId, count == null ? 1 : count.intValue() + 1);
    }

    /**
     * 记录服务的每次请求时间戳（用于心跳分析）
     * @param routeId
     */
    public void timeLog(String routeId){
        RouteReqCache.put(routeId, System.currentTimeMillis());
    }
}
