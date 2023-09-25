package com.alinesno.infra.base.gateway.proxy.gateway.filter.factory;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.gateway.formwork.util.ApiResult;
import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.formwork.util.HttpResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description 自定义网关限流过滤器工厂类，用于个性化自定义限流输出包装使用，默认使用：RequestRateLimiterGatewayFilterFactory
 * @author  JL
 * @date 2020/11/19
 * @version 1.0.0
 */
@Slf4j
@Component
public class CustomRequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomRequestRateLimiterGatewayFilterFactory.Config> {

    private final RateLimiter defaultRateLimiter;
    private final KeyResolver defaultKeyResolver;

    public CustomRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter, @Qualifier("hostAddrKeyResolver") KeyResolver defaultKeyResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    public KeyResolver getDefaultKeyResolver() {
        return defaultKeyResolver;
    }

    public RateLimiter getDefaultRateLimiter() {
        return defaultRateLimiter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver resolver = (config.keyResolver == null) ? defaultKeyResolver : config.keyResolver;
        RateLimiter<Object> limiter = (config.rateLimiter == null) ? defaultRateLimiter : config.rateLimiter;
        return (exchange, chain) -> {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            return resolver.resolve(exchange).flatMap(key -> {
                if(StrUtil.isEmpty(key)){
                    log.warn("网关转发客户端ID:[{}],请求地址：[{}]，执行限流验证异常：获取请求requestId异常", new Object[]{route.getId(), route.getUri().toString()});
                    String msg = "网关转发客户端，执行限流验证异常：获取请求requestId异常";
                    return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
                }
                return limiter.isAllowed(route.getId(), key).flatMap(response -> {
                    for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                        exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
                    }
                    if (response.isAllowed()) {
                        return chain.filter(exchange);
                    }
                    log.warn("网关转发客户端ID:[{}],请求地址：[{}]，执行验证异常：访问已限流，请稍候再请求", new Object[]{route.getId(), route.getUri().toString()});
                    String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.FAILED, "网关转发客户端，执行验证异常：访问已限流，请稍候再请求", null));
                    return HttpResponseUtils.write(exchange.getResponse(), config.getStatusCode(), jsonMsg);
                    //return httpResponse.setComplete();
                });
            });
        };
    }

    public static class Config {
        private KeyResolver keyResolver;
        private RateLimiter rateLimiter;
        private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

        public KeyResolver getKeyResolver() {
            return keyResolver;
        }

        public Config setKeyResolver(KeyResolver keyResolver) {
            this.keyResolver = keyResolver;
            return this;
        }
        public RateLimiter getRateLimiter() {
            return rateLimiter;
        }

        public Config setRateLimiter(RateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
            return this;
        }

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public Config setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
            return this;
        }
    }
}
