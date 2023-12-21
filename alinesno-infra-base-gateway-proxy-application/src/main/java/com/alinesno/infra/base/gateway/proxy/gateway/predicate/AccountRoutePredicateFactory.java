package com.alinesno.infra.base.gateway.proxy.gateway.predicate;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author  luoxiaodong
 * @data 2023/04/25 10:33
 */
@Slf4j
@Component
public class AccountRoutePredicateFactory extends AbstractRoutePredicateFactory<AccountRoutePredicateFactory.Config> {

    public AccountRoutePredicateFactory() {
        super(Config.class);
    }


    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("name");
    }


    @Override
    public Predicate<ServerWebExchange> apply(Config config) {

        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange serverWebExchange) {

//                ServerHttpRequest request = serverWebExchange.getRequest();
//                HttpHeaders httpHeaders = request.getHeaders();
//                List<String> authHeaders = httpHeaders.get(Constants.AUTH_GATEWAY);
//                if (CollectionUtil.isEmpty(authHeaders)) {
//                    log.warn("请求中缺少用户验证token");
//                    return false;
//                }
//
//                String token = authHeaders.get(0);
//                String routeId = serverWebExchange.getRequiredAttribute(GATEWAY_PREDICATE_ROUTE_ATTR);
//                if (AccountCache.getRouteCache(token).contains(routeId)) {
//                    log.info("用户token验证通过 - routeId: {}",routeId);
//                    return true;
//                }

                return true ;
            }
        };

    }


    public static class Config {

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
