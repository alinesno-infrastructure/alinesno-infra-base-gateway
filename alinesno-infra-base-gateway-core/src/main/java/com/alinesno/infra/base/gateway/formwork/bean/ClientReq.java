package com.alinesno.infra.base.gateway.formwork.bean;

import com.alinesno.infra.base.gateway.formwork.entity.Client;

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
public class ClientReq extends Client implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
