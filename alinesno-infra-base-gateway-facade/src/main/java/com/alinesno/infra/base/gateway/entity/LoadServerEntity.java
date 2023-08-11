package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 负载服务实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("load_server")
public class LoadServerEntity extends InfraBaseEntity {

    @TableField("route_id")
    private Long routeId;

    @TableField("balanced_id")
    private Long balancedId;

    @TableField("weight")
    private Integer weight;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    // 省略getter和setter方法

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getBalancedId() {
        return balancedId;
    }

    public void setBalancedId(Long balancedId) {
        this.balancedId = balancedId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
