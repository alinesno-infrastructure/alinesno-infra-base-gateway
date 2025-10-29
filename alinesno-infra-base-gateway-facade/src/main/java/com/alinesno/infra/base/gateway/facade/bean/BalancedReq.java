package com.alinesno.infra.base.gateway.facade.bean;

import com.alinesno.infra.base.gateway.facade.entity.Balanced;
import com.alinesno.infra.base.gateway.facade.entity.LoadServer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description
 * @author jianglong
 * @date 2020/06/28
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BalancedReq extends Balanced implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
    private List<LoadServer> serverList;
}
