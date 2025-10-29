package com.alinesno.infra.base.gateway.facade.bean;

import lombok.Data;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Data
public class BaseReq {

    //  操作员
    private long operatorId; // 操作员ID
    private long deptId ; // 部门ID
    private long orgId ; // 机构ID

}
