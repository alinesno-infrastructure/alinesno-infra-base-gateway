package com.alinesno.infra.base.gateway.formwork.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetAddress;

/**
 * @description 获取请求客户端真实IP地址
 * @author jianglong
 * @author luoxiaodong
 * @date 2020/05/07
 * @version v1.0.0
 */
public class NetworkIpUtils {

    private static final String UNKNOWN = "unknown";
    private static final int IP_MAX_LEGNTH = 15;

    /**
     * 如果通过代理进来或多层路由代理，则透过防火墙获取真实IP地址
     * @param request
     * @return
     */
    public final static String getIpAddress(ServerHttpRequest request) {
        //X-Forwarded-For：Squid 服务代理
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                //Proxy-Client-IP：apache 服务代理
                ip = request.getHeaders().getFirst("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                //WL-Proxy-Client-IP：weblogic 服务代理
                ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                //HTTP_CLIENT_IP：第三方中转代理服务器
                ip = request.getHeaders().getFirst("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                //X-Real-IP：nginx服务代理
                ip = request.getHeaders().getFirst("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
                InetAddress address = request.getRemoteAddress().getAddress();
                ip = address.getHostAddress();
            }
        }
        //225.225.225.225
        if (ip.length() > IP_MAX_LEGNTH) {
            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                if (!(UNKNOWN.equalsIgnoreCase(ips[i]))) {
                    return ips[i];
                }
            }
        }
        return ip;
    }

}
