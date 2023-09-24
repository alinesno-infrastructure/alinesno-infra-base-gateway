package com.alinesno.infra.base.gateway.formwork.bean;

import lombok.Data;

/**
 * @description 鉴权器开关
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
public class RouteAccessBean {
    private Boolean headerChecked;
    private Boolean ipChecked;
    private Boolean parameterChecked;
    private Boolean timeChecked;
    private Boolean cookieChecked;
}
