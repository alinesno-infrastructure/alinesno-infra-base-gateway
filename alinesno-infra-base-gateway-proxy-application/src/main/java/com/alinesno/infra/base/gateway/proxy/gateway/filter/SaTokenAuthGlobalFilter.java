//package com.alinesno.infra.base.gateway.proxy.gateway.filter;
//
//import cn.dev33.satoken.exception.NotLoginException;
//import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
//import cn.dev33.satoken.stp.StpUtil;
//import com.alinesno.infra.base.gateway.core.util.HttpResponseUtils;
//import com.alinesno.infra.base.gateway.proxy.gateway.config.AlinesnoSecurityProperties;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
///**
// * 鉴权过滤器
// */
//@Slf4j
//@Component
//@Order(Ordered.LOWEST_PRECEDENCE)
//public class SaTokenAuthGlobalFilter implements GlobalFilter, Ordered {
//
//    private final AlinesnoSecurityProperties securityProperties;
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    public SaTokenAuthGlobalFilter(AlinesnoSecurityProperties securityProperties) {
//        this.securityProperties = securityProperties;
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.LOWEST_PRECEDENCE;
//    }
//
//    /**
//     * 鉴权过滤器
//     * @param exchange
//     * @param chain
//     * @return
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getPath().toString();
//
//        // 白名单路径直接放行
//        if (isWhitePath(path)) {
//            return chain.filter(exchange);
//        }
//
//        try {
//            SaReactorSyncHolder.setContext(exchange);
//            // 获取token并验证
//            String token = StpUtil.getTokenValue() ;
//            log.debug("token:{}", token);
//
//            log.debug("用户验证是否登陆:{}" , StpUtil.isLogin());
//            StpUtil.checkLogin();
//
//            return chain.filter(exchange);
//
//        } catch (NotLoginException e) {
//            log.error("用户未登录" , e);
//
//            String msg = "网关验证失败:用户未登录";
//            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
//        } finally {
//            SaReactorSyncHolder.clearContext();
//        }
//    }
//
//    /**
//     * 判断是否是白名单路径
//     * @param path
//     * @return
//     */
//    private boolean isWhitePath(String path) {
//        List<String> excludes = securityProperties.getExcludes();
//
//        for (String pattern : excludes) {
//            // 支持 Ant 风格匹配，比如 /sso/**, /logout
//            if (pathMatcher.match(pattern, path)) {
//                return true;
//            }
//        }
//
//        return false ;
//    }
//
//}