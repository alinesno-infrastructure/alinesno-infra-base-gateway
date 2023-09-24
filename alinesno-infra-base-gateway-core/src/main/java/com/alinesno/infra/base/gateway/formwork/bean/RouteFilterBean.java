package com.alinesno.infra.base.gateway.formwork.bean;

import lombok.Data;

/**
 * @description 过滤器开关
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
public class RouteFilterBean {
    private Boolean ipChecked;
    private Boolean tokenChecked;
    private Boolean idChecked;
}
