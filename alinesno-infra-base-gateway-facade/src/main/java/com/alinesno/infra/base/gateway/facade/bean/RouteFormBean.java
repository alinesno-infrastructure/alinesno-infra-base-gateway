package com.alinesno.infra.base.gateway.facade.bean;

import com.alinesno.infra.base.gateway.facade.entity.Monitor;
import com.alinesno.infra.base.gateway.facade.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteFormBean extends Route implements java.io.Serializable {
    private Monitor monitor;
}
