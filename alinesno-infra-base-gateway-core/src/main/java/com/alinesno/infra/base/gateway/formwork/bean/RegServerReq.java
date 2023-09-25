package com.alinesno.infra.base.gateway.formwork.bean;

import com.alinesno.infra.base.gateway.formwork.entity.RegServer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description
 * @author jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RegServerReq extends RegServer implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
