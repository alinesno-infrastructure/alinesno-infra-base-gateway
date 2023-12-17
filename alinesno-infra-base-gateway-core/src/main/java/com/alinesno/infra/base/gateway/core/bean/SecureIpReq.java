package com.alinesno.infra.base.gateway.core.bean;

import com.alinesno.infra.base.gateway.core.entity.SecureIp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description 对前端请求进行接收与封装bean
 * @author jianglong
 * @date 2020/05/28
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SecureIpReq extends SecureIp implements java.io.Serializable {
    private Integer currentPage;
    private Integer pageSize;
}
