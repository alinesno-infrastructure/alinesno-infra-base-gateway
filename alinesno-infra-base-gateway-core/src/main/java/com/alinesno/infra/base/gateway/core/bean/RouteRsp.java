package com.alinesno.infra.base.gateway.core.bean;

import com.alinesno.infra.base.gateway.core.entity.Monitor;
import com.alinesno.infra.base.gateway.core.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description
 * @author JL
 * @date 2021/04/16
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteRsp extends Route {
    private Monitor monitor;
}
