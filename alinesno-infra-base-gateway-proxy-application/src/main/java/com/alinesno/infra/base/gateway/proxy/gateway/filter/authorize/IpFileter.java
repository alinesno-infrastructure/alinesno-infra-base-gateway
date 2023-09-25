package com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Arrays;

/**
 * @description 验证当前IP是否可访问（只限定指定IP访问）
 * @author  jianglong
 * @date 2020/05/25
 * @version 1.0.0
 */
@Slf4j
public class IpFileter extends FilterHandler {

    public IpFileter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        if (route.getFilterAuthorizeName().contains("ip")){
            log.info("处理网关路由请求{},执行ip过滤 ", route.getId());
            if(StrUtil.isEmpty(route.getAccessIp())){
                log.warn("执行ip过滤 -- 自定义ip验证不通过: 不存在的校验式");
                return;
            }

            String originIp = NetworkIpUtils.getIpAddress(request);
            String cidrStr = route.getAccessIp();
            String[] cidrS;
            if(cidrStr.contains(",")){
                cidrS = Arrays.stream(cidrStr.split(",")).map(ipRex -> ipRex.strip()).toArray(String[]::new);
            } else {
                cidrS= new String[]{cidrStr.strip()};
            }
            if ( Arrays.stream(cidrS).filter(cidr -> {
                int mark = cidr.indexOf('/');
                if (mark < 0) {
                    cidr = cidr + "/32";
                }
                return NetUtil.isInRange(originIp, cidr);
            }).count() > 0 ){
            }else {
                throw new IllegalStateException("执行ip过滤,自定义ip验证不通过 请求源="+originIp+",自定义目标=" + route.getAccessIp());
            }
        }
    }

}
