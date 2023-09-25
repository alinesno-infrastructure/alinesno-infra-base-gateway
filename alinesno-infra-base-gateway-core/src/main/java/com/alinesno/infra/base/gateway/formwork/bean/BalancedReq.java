package com.alinesno.infra.base.gateway.formwork.bean;

import java.util.List;

import com.alinesno.infra.base.gateway.formwork.entity.Balanced;
import com.alinesno.infra.base.gateway.formwork.entity.LoadServer;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
