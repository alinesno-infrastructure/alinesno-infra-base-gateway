package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

/**
 * 客户端注册网关路由服务实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("reg_server")
public class RegServerEntity extends InfraBaseEntity {

    /**
     * 客户端ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 路由ID
     */
    @TableField("route_id")
    private String routeId;

    /**
     * token加密内容
     */
    @TableField("token")
    private String token;

    /**
     * token加密密钥
     */
    @TableField("secret_key")
    private String secretKey;

    /**
     * token有效期截止时间
     */
    @TableField("token_effective_time")
    private Date tokenEffectiveTime;

    // 省略getter和setter方法

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Date getTokenEffectiveTime() {
        return tokenEffectiveTime;
    }

    public void setTokenEffectiveTime(Date tokenEffectiveTime) {
        this.tokenEffectiveTime = tokenEffectiveTime;
    }
}
