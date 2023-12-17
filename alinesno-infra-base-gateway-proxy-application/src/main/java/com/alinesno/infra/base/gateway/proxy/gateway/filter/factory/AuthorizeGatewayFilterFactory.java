package com.alinesno.infra.base.gateway.proxy.gateway.filter.factory;

import com.alinesno.infra.base.gateway.core.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.core.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @description 配置鉴权工厂类，从而实现自定义全局的鉴权管理
 * @author  jianglong
 * @date 2020/05/07
 * @version 1.0.0
 */
@Slf4j
@Component
public class AuthorizeGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizeGatewayFilterFactory.Config> {

    /**
     在yml中配置
     filters:
        # 关键在下面一句，值为true则开启认证，false则不开启
        # 这种配置方式和spring cloud gateway内置的GatewayFilterFactory一致
        - Authorize=true
    */

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public AuthorizeGatewayFilterFactory() {
        super(Config.class);
        log.info("Loaded GatewayFilterFactory [Authorize]");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    /**
     * 核心执行方法
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //判断是否开启了签权验证，- Authorize=true，只有为true才继续执行，否则直接跳过
            if (config.isEnabled()) {
                ServerHttpRequest request = exchange.getRequest();
                String clientIp = NetworkIpUtils.getIpAddress(request);
                Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
                FilterHandler headerFilter = this.createHandler();
                //获取缓存中的指定key路由对象
                Object obj = RouteCache.get(route.getId());
                if (obj == null){
                    return HttpResponseUtils.writeUnauth(exchange.getResponse(), "未获取到指定网关注册服务路由信息或路由请求无效！");
                }
                try {
                    //执行header,ip,parameter,time,cookie验证
                    headerFilter.handler(request, (com.alinesno.infra.base.gateway.core.entity.Route) obj);
                }catch(Exception e){
                    log.warn("网关转发客户端【{}】路由请求【{}】，执行验证异常：", clientIp, route.getId(), e);
                    return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关转发客户端【"+clientIp+"】路由请求【"+route.getId()+"】，执行验证异常：" + e.getMessage());
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 控制是否开启认证
        private boolean enabled;
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 责任链初始化
     * @return
     */
    public FilterHandler createHandler(){
        FilterHandler cookieFilter = new CookieFilter(null);
        FilterHandler parameterFilter = new ParameterFilter(cookieFilter);
        FilterHandler ipFileter = new IpFileter(parameterFilter);
        FilterHandler timeFilter = new TimeFilter(ipFileter);
        FilterHandler headerFilter = new HeaderFilter(timeFilter);
        return headerFilter;
    }

}
