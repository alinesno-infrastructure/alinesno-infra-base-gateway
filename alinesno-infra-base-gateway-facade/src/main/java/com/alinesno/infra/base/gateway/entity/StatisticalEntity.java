package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 统计数据实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("statistical_data")
public class StatisticalEntity extends InfraBaseEntity {

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 统计时间键
     */
    private String countTimeKey;

    /**
     * 总计数
     */
    private Long totalCount;

    /**
     * 成功计数
     */
    private Long successCount;

    /**
     * 失败计数
     */
    private Long failCount;

    // 省略getter和setter方法

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCountTimeKey() {
        return countTimeKey;
    }

    public void setCountTimeKey(String countTimeKey) {
        this.countTimeKey = countTimeKey;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
}
