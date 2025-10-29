package com.alinesno.infra.base.gateway.facade.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description 监控开关
 * @author JL
 * @date 2021/04/14
 * @version v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MonitorBean implements java.io.Serializable {
    private Boolean checked;
}
