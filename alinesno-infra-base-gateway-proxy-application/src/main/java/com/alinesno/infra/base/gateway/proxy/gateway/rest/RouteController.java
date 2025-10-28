package com.alinesno.infra.base.gateway.proxy.gateway.rest;

import com.alinesno.infra.base.gateway.adapter.dto.ApiResult;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.service.RouteService;
import com.alinesno.infra.base.gateway.proxy.gateway.event.DataRouteApplicationEventListen;
import com.alinesno.infra.base.gateway.proxy.gateway.service.DynamicRouteService;
import com.alinesno.infra.base.gateway.proxy.gateway.service.LoadRouteService;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GatewayRouteDefinition;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @description 动态添加路由
 * @author  jianglong
 * @author luoxiaodong
 * @date 2020/05/11
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/base/gateway/proxy/route")
public class RouteController {

    @Resource
    private DynamicRouteService dynamicRouteService;

    @Resource
    private LoadRouteService loadRouteService;

    @Resource
    private RouteService routeService;

    @Resource
    private DataRouteApplicationEventListen redisRouteDefinitionRepository;

    /**
     add-json:
     {
     "filters":[
         {
             "name":"StripPrefix",
             "args":{
             "_genkey_0":"1"
             }
         }
     ],
     "id":"hi_producer",
     "uri":"lb://service-hi",
     "order":1,
     "predicates":[
         {
             "name":"Path",
             "args":{
                "pattern":"/hi/producer/**"
             }
         }
     ]
     }
     */

    /**
     * 增加路由
     * @param gwdefinition
     * @return
     */
    @PostMapping("/add")
    public ApiResult add(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.add(definition);
        return new ApiResult();
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    @DeleteMapping("/routes/{id}")
    public ApiResult delete(@PathVariable String id) {
        this.dynamicRouteService.delete(id);
        return new ApiResult();
    }

    /**
     * 更新路由
     * @param gwdefinition
     * @return
     */
    @PostMapping("/update")
    public ApiResult update(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.update(definition);
        return new ApiResult();
    }

    /**
     * 获取所有路由信息
     * @return
     */
    @GetMapping(value = "/list")
    public Flux<RouteDefinition> list() {
        return redisRouteDefinitionRepository.getRouteDefinitions();
    }

    /**
     * 从数据库加载指定路由
     * @return
     */
    @GetMapping(value = "/load")
    public ApiResult load(@RequestParam String id) {
        Assert.notNull(id, "路由ID不能为空");
        Route route = routeService.findById(id);
        RouteDefinition routeDefinition = loadRouteService.loadRouteDefinition(route);
        this.dynamicRouteService.add(routeDefinition);
        return new ApiResult();
    }

    /**
     * 刷新路由(可刷新：ip,client,route)
     * @return
     */
    @GetMapping(value = "/fresh")
    public ApiResult fresh(@RequestParam(required = false) String type) {
        this.dynamicRouteService.fresh(type);
        return new ApiResult();
    }

}
