package com.alinesno.infra.base.gateway.formwork.bean;

import lombok.Data;

/**
 * @description 限流器开关
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
public class RouteLimiterBean {
    private Boolean ipChecked;
    private Boolean uriChecked;
    private Boolean idChecked;
}
