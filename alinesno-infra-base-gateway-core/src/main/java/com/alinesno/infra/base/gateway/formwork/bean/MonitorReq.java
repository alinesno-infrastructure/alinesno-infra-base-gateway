package com.alinesno.infra.base.gateway.formwork.bean;

import lombok.Data;

/**
 * @description 接口监控请求参数封装
 * @author JL
 * @date 2021/04/14
 * @version v1.0.0
 */
@Data
public class MonitorReq extends BaseReq {
    private String status;
}
