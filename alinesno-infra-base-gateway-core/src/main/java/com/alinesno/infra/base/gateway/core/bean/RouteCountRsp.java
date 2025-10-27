package com.alinesno.infra.base.gateway.core.bean;

import com.alinesno.infra.base.gateway.core.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description
 * @author JL
 * @date 2020/12/30
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RouteCountRsp extends Route implements java.io.Serializable {
    /**
     * 统计量
     */
    private Integer count;

}
