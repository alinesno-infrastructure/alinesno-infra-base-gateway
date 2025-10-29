package com.alinesno.infra.base.gateway.proxy.gateway.filter;

import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.core.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.core.util.NetworkIpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * @description 限制IP访问，只能内网或指定IP访问
 * @author  jianglong
 * @date 2020/06/01
 * @version v1.0.0
 */
@Slf4j
@Component
public class AuthIpWebFilter implements WebFilter {

    @Value("${system.auth.ip:}")
    private String authIps;

    /**
     * 执行过滤的核心方法
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = NetworkIpUtils.getIpAddress(request);
        if (!this.isPassIp(ip)){
            String msg = "客户端IP无权限访问："+ request.getURI().getPath() +"! Ip:" + ip ;
            log.error("{}, 如需访问，请yml配置文件中system.auth.ip里添加ip，为预防风险，建议指定内网IP访问！", msg);
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 验证IP是否有权限访问
     * 逻辑：
     * - 如果authIps为空（未配置） -> 不做校验，默认允许访问（返回 true）
     * - 如果配置了值，则按逗号等分隔符分割每项进行比对。支持精确匹配和带 * 的通配符（* 表示任意字符序列）
     *
     * @param ip
     * @return
     */
    public boolean isPassIp(String ip){
        // 未配置则默认不校验，允许访问
        if (StringUtils.isBlank(authIps)){
            log.warn("未配置网关访问IP白名单，默认允许访问");
            return true;
        }

        String[] ips = authIps.split(Constants.SEPARATOR_SIGN);
        for (String raw : ips) {
            if (raw == null) continue;
            String value = raw.trim();
            if (StringUtils.isEmpty(value)) continue;

            // 精确匹配
            if (StringUtils.equals(value, ip)) {
                return true;
            }

            // 通配符支持（* -> .*)
            if (value.contains("*")) {
                // 使用 Pattern.quote 先转义，再把转义后的 \* 替换为 .*，加上 ^$ 保证整行匹配
                String regex = "^" + Pattern.quote(value).replace("\\*", ".*") + "$";
                if (Pattern.matches(regex, ip)) {
                    return true;
                }
            }
        }

        return false;
    }

}