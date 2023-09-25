package com.alinesno.infra.base.gateway.proxy.gateway.filter.factory;

import com.alinesno.infra.base.gateway.formwork.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.formwork.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.HystrixObservableCommand.Setter;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.netflix.hystrix.exception.HystrixRuntimeException.FailureType.TIMEOUT;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * @description 自定义熔断器网关过滤工厂类，用于替换HystrixGatewayFilterFactory类中，解决无法动态拓展config.setter属性值的问题
 * @author  jianglong
 * @date 2020/05/27
 * @version 1.0.0
 */
@Slf4j
@Component
public class CustomHystrixGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomHystrixGatewayFilterFactory.Config> {

    /**
     在yml中配置
     filters:
     # 这种配置方式和spring cloud gateway内置的GatewayFilterFactory一致
     - name: CustomHystrix
     */

    public static final String FALLBACK_URI = "fallbackUri";

    @Autowired
    private DispatcherHandler dispatcherHandler;

    public CustomHystrixGatewayFilterFactory() {
        super(Config.class);
        log.info("Loaded GatewayFilterFactory [CustomHystrix]");
    }

//    public CustomHystrixGatewayFilterFactory(DispatcherHandler dispatcherHandler) {
//        super(Config.class);
//        this.dispatcherHandler = dispatcherHandler;
//        log.info("Loaded GatewayFilterFactory [CustomHystrix]");
//    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(NAME_KEY);
    }

    @Override
    public GatewayFilter apply(String routeId, Consumer<Config> consumer) {
        Config config = newConfig();
        consumer.accept(config);
        if (StringUtils.isEmpty(config.getName()) && !StringUtils.isEmpty(routeId)) {
            config.setName(routeId);
        }
        return apply(config);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //TODO: if no name is supplied, generate one from command id (useful for default filter)
        return (exchange, chain) -> {
            if (config.setter == null) {
                if (config.name == null) {
                    return HttpResponseUtils.writeError(exchange.getResponse(), "A name must be supplied for the Hystrix Command Key");
                }
                HystrixCommandProperties.Setter propertiesSetter = HystrixCommandProperties.Setter();
                if (config.name.startsWith(RouteConstants.Hystrix.CUSTOM_HYSTRIX_NAME)){
                    Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
                    //添加自定义熔断器
                    com.alinesno.infra.base.gateway.formwork.entity.Route cacheRoute = (com.alinesno.infra.base.gateway.formwork.entity.Route)RouteCache.get(route.getId());
                    //HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(config.name);
                    //HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(config.name);
                    //启用回调
                    propertiesSetter.withFallbackEnabled(true);
                    //启用超时检测
                    propertiesSetter.withExecutionTimeoutEnabled(true);
                    //设置超时时长
                    propertiesSetter.withExecutionTimeoutInMilliseconds(cacheRoute.getFallbackTimeout().intValue());
                    //config.setter = Setter.withGroupKey(groupKey).andCommandKey(commandKey).andCommandPropertiesDefaults(propertiesSetter);
                }
                //默认全局熔断器
                HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(getClass().getSimpleName());
                HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(config.name);
                config.setter = Setter.withGroupKey(groupKey).andCommandKey(commandKey).andCommandPropertiesDefaults(propertiesSetter);
            }

            RouteHystrixCommand command = new RouteHystrixCommand(config.setter, config.fallbackUri, exchange, chain);
            return Mono.create(s -> {
                Subscription sub = command.toObservable().subscribe(s::success, s::error, s::success);
                s.onCancel(sub::unsubscribe);
            }).onErrorResume((Function<Throwable, Mono<Void>>) throwable -> {
                if (throwable instanceof HystrixRuntimeException) {
                    HystrixRuntimeException e = (HystrixRuntimeException) throwable;
                    if (e.getFailureType() == TIMEOUT) { //TODO: optionally set status
                        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
                        String clientIp = NetworkIpUtils.getIpAddress(exchange.getRequest());
                        log.error(String.format("网关转发客户端【%s】路由请求【%s】，触发超时熔断机制异常：", clientIp, route.getId()), e);
                        setResponseStatus(exchange, HttpStatus.GATEWAY_TIMEOUT);
                        return exchange.getResponse().setComplete();
                    }
                }
                return Mono.error(throwable);
            }).then();
        };
    }

    //TODO: replace with HystrixMonoCommand that we write
    private class RouteHystrixCommand extends HystrixObservableCommand<Void> {

        private final URI fallbackUri;
        private final ServerWebExchange exchange;
        private final GatewayFilterChain chain;

        RouteHystrixCommand(Setter setter, URI fallbackUri, ServerWebExchange exchange, GatewayFilterChain chain) {
            super(setter);
            this.fallbackUri = fallbackUri;
            this.exchange = exchange;
            this.chain = chain;
        }

        @Override
        protected Observable<Void> construct() {
            return RxReactiveStreams.toObservable(this.chain.filter(exchange));
        }

        @Override
        protected Observable<Void> resumeWithFallback() {
            if (this.fallbackUri == null) {
                return super.resumeWithFallback();
            }

            //TODO: copied from RouteToRequestUrlFilter
            URI uri = exchange.getRequest().getURI();
            //TODO: assume always?
            boolean encoded = containsEncodedParts(uri);
            URI requestUrl = UriComponentsBuilder.fromUri(uri)
                    .host(null)
                    .port(null)
                    .uri(this.fallbackUri)
                    .build(encoded)
                    .toUri();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);

            ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
            ServerWebExchange mutated = exchange.mutate().request(request).build();
            return RxReactiveStreams.toObservable(CustomHystrixGatewayFilterFactory.this.dispatcherHandler.handle(mutated));
        }
    }

    public static class Config {
        private String name;
        private Setter setter;
        private URI fallbackUri;

        public String getName() {
            return name;
        }

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public Config setFallbackUri(String fallbackUri) {
            if (fallbackUri != null) {
                setFallbackUri(URI.create(fallbackUri));
            }
            return this;
        }

        public URI getFallbackUri() {
            return fallbackUri;
        }

        public void setFallbackUri(URI fallbackUri) {
            if (fallbackUri != null && !"forward".equals(fallbackUri.getScheme())) {
                throw new IllegalArgumentException("Hystrix Filter currently only supports 'forward' URIs, found " + fallbackUri);
            }
            this.fallbackUri = fallbackUri;
        }

        public Config setSetter(Setter setter) {
            this.setter = setter;
            return this;
        }
    }

}
