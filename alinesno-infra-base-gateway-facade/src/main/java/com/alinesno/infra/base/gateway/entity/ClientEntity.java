package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 客户端实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("client")
public class ClientEntity extends InfraBaseEntity {

    @TableField("system_code")
    private String systemCode;

    @TableField("name")
    private String name;

    @TableField("group_code")
    private String groupCode;

    @TableField("ip")
    private String ip;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    // 省略getter和setter方法

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}