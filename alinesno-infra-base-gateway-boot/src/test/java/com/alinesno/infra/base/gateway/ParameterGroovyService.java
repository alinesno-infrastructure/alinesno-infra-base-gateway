package com.alinesno.infra.base.gateway;

import com.alinesno.infra.base.gateway.core.base.BaseGroovyService;
import com.alinesno.infra.base.gateway.core.util.NetworkIpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
/**
 * @description
 * @author  admin
 * @version 1.0.0
*/
public class ParameterGroovyService extends BaseGroovyService {
    private Logger log = LoggerFactory.getLogger("ParameterGroovyService");
    @Override
    public void apply(ServerWebExchange exchange) throws Exception {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String remoteAddr = NetworkIpUtils.getIpAddress(request);
        
        //routeId, ruleName, extednInfo从继承父类BaseGroovyService中获取
        log.info("网关路由【{}】执行GroovySrcipt规则引擎动态脚本组件名称【{}】,扩展参数【{}】", routeId, ruleName, extednInfo);
        
        Map<String,String> valueMap = request.getQueryParams().toSingleValueMap();
        String userId = valueMap.get("userId");
        if (StringUtils.isBlank(userId)){
            throw new IllegalArgumentException("缺少userId参数");
        }
    }
}