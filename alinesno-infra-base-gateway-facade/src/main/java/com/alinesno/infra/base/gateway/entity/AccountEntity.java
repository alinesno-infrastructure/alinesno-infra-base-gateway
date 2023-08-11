package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 账户实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("account")
public class AccountEntity extends InfraBaseEntity {

    /**
     * 账户ID
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 授权令牌
     */
    @TableField("auth_token")
    private String authToken;

    // 省略getter和setter方法

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}