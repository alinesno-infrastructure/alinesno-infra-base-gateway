package com.alinesno.infra.base.gateway.manage.filter;

import com.alinesno.infra.base.gateway.core.util.Constants;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            log.error(msg + ", 如需访问，请yml配置文件中system.auth.ip里添加ip，为预防风险，建议指定内网IP访问！");
            return HttpResponseUtils.writeUnauth(exchange.getResponse(), msg);
        }
        return chain.filter(exchange);
    }

    /**
     * 验证IP是否有权限访问
     * @param ip
     * @return
     */
    public boolean isPassIp(String ip){
        if (StringUtils.isBlank(authIps)){
            return false;
        }
        List<String> ipList = new ArrayList<>();
        if (authIps.contains(Constants.SEPARATOR_SIGN)){
            String [] ips = authIps.split(Constants.SEPARATOR_SIGN);
            ipList = Arrays.asList(ips);
        }
        boolean isPass = false;
        for (String value : ipList){
            if(StringUtils.equals(value, ip)) {
                isPass = true;
                //如果有*号，表示支持正则表达示配置（只支持*号表达式）
            }else if (value.indexOf("*") != -1){
                isPass = Pattern.matches(value, ip);
            }
            if (isPass){
                break;
            }
        }
        return isPass;
    }

}
