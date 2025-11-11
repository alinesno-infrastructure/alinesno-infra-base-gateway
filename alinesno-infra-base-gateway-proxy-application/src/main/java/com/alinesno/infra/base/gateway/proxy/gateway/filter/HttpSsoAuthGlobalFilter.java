//package com.alinesno.infra.base.gateway.proxy.gateway.filter;
//
//import com.alinesno.infra.base.gateway.core.util.HttpResponseUtils;
//import com.alinesno.infra.base.gateway.proxy.gateway.config.AlinesnoSecurityProperties;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//@Order(Ordered.LOWEST_PRECEDENCE)
//public class HttpSsoAuthGlobalFilter implements GlobalFilter, Ordered {
//
////    sa-token:
////    token-name: Authorization
////    host-path: https://alinesno-infra-base-identity-auth-application.linesno.com
////    sso:
////    auth-url: ${sa-token.host-path}/login
////    is-http: true
////    check-ticket-url: ${sa-token.host-path}/prod-api/sso/checkTicket
//
//    @Value("${sa-token.sso.check-ticket-url}")
//    private String getCheckTicketUrl ;
//
//    private final AlinesnoSecurityProperties securityProperties;
//    private final WebClient webClient;
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    public HttpSsoAuthGlobalFilter(AlinesnoSecurityProperties securityProperties, WebClient.Builder webClientBuilder) {
//        this.securityProperties = securityProperties;
//        this.webClient = webClientBuilder.build();
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.LOWEST_PRECEDENCE;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getPath().toString();
//        if (isWhitePath(path)) {
//            return chain.filter(exchange);
//        }
//
//        // 从 Header/Cookie/Query 拿到 token（示例使用 Authorization header）
//        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if (token == null || token.trim().isEmpty()) {
//            return HttpResponseUtils.writeUnauth(exchange.getResponse(), "缺少 token");
//        }
//
//        // 根据你的 checkTicket URL 调用认证中心，示例使用 GET?ticket=xxx
//        String checkUrl = getCheckTicketUrl; // 根据你的配置读取
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder.path(checkUrl).queryParam("ticket", token).build())
//                .retrieve()
//                .bodyToMono(String.class) // 先拿原始字符串，后面按实际格式解析
//                .flatMap(body -> {
//                    // 这里需要根据认证中心的返回格式判断是否登录成功
//                    // 假设返回 JSON: { code: 200, data: { userId: 123, username: "abc" } }
//                    boolean ok = parseSuccess(body); // 自行实现解析
//                    if (!ok) {
//                        return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关验证失败: 用户未登录或票据非法");
//                    }
//
//                    // 从返回中解析用户信息（示例）
//                    String userId = parseUserId(body);
//                    String username = parseUsername(body);
//
//                    // 将用户信息注入到转发请求头，供下游使用
//                    ServerHttpRequest mutated = exchange.getRequest().mutate()
//                            .header("X-User-Id", userId)
//                            .header("X-User-Name", username)
//                            .build();
//
//                    ServerWebExchange newExchange = exchange.mutate().request(mutated).build();
//
//                    return chain.filter(newExchange);
//                })
//                .onErrorResume(ex -> {
//                    // 调用失败处理
//                    return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关验证失败: 调用认证中心异常");
//                });
//    }
//
//    private boolean isWhitePath(String path) {
//        List<String> excludes = securityProperties.getExcludes();
//        for (String pattern : excludes) {
//            if (pathMatcher.match(pattern, path)) return true;
//        }
//        return false;
//    }
//
//    // parseSuccess / parseUserId / parseUsername 根据你的认证中心的返回体实现
//}