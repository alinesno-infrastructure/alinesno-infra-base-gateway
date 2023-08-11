package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 安全IP实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("secure_ip")
public class SecureIpEntity extends InfraBaseEntity {

    /**
     * IP地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 用户标识
     */
    @TableField("account_token")
    private String accountToken;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    // 省略getter和setter方法

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
