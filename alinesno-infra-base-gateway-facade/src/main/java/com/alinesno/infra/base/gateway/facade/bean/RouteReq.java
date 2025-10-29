package com.alinesno.infra.base.gateway.facade.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description
 * @author jianglong
 * @date 2020/05/11
 * @version v1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RouteReq extends BaseReq implements java.io.Serializable  {

    //表单值
    private RouteFormBean form;
    //过滤器开关
    private RouteFilterBean filter;
    //熔断器开关
    private RouteHystrixBean hystrix;
    //限流器开关
    private RouteLimiterBean limiter;
    //鉴权器开关
    private RouteAccessBean access;
    //监控开关
    private MonitorBean monitor;

    private Integer currentPage;
    private Integer pageSize;
}
