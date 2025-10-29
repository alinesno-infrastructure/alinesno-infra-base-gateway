package com.alinesno.infra.base.gateway.facade.bean;

import com.alinesno.infra.base.gateway.facade.entity.LoadServer;
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
public class LoadServerReq extends LoadServer implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
