package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * Groovy脚本实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("groovy_script")
public class GroovyScriptEntity extends InfraBaseEntity {

    /**
     * 网关ID
     */
    @TableField("route_id")
    private Long routeId;

    /**
     * 脚本名称
     */
    @TableField("name")
    private String name;

    /**
     * 脚本内容
     */
    @TableField("content")
    private String content;

    /**
     * 扩展内容，参数json
     */
    @TableField("extend_info")
    private String extendInfo;

    /**
     * 执行事件，request|response
     */
    @TableField("event")
    private String event;

    /**
     * 顺序
     */
    @TableField("order_num")
    private Integer orderNum;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
