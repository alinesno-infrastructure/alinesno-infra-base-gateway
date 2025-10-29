package com.alinesno.infra.base.gateway.facade.bean;

import lombok.Data;

/**
 * @Description熔断器开关
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
public class RouteHystrixBean {
    private Boolean defaultChecked;
    private Boolean customChecked;
}
