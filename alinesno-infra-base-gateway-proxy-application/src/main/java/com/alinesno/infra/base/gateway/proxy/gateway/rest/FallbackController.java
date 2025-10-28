package com.alinesno.infra.base.gateway.proxy.gateway.rest;

import com.alinesno.infra.base.gateway.adapter.dto.ApiResult;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @description 触发熔断机制响应控制器
 * @author  jianglong
 * @date 2020/05/26
 * @version 1.0.0
 */
@Slf4j
@RestController
@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
public class FallbackController {

    /**
     * 触发熔断机制的回调方法
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:8771", "null"})
    @RequestMapping(value = "/fallback", method = {RequestMethod.GET,RequestMethod.POST})
    public ApiResult fallback(@RequestParam(required = false) String routeId) {
        log.warn("触发熔断机制的回调方法:fallback,routeId={}", routeId);
        return new ApiResult(Constants.FAILED,"提示：服务响应超时，触发熔断机制，请联系运维人员处理。此消息由网关服务返回！",null);
    }

    /**
     * 触发自定义熔断机制的回调方法
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:8771", "null"})
    @RequestMapping(value = "/fallback/custom", method = {RequestMethod.GET,RequestMethod.POST})
    public ApiResult fallbackCustom(@RequestParam String routeId) {
        log.warn("触发自定义熔断机制的回调方法:fallback,routeId={}", routeId);
        Route route = (Route) RouteCache.get(routeId);
        if (route != null){
            return new ApiResult(Constants.FAILED,"提示：" + route.getFallbackMsg(),null);
        }
        return new ApiResult(Constants.FAILED,"提示：服务响应超时，触发自定义熔断机制，请联系运维人员处理。此消息由网关服务返回！",null);
    }

}
