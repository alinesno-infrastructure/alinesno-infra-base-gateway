package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 负载均衡实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("balanced")
public class BalancedEntity extends InfraBaseEntity {

    @TableField("name")
    private String name;

    @TableField("group_code")
    private String groupCode;

    @TableField("load_uri")
    private String loadUri;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    // 省略getter和setter方法

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

    public String getLoadUri() {
        return loadUri;
    }

    public void setLoadUri(String loadUri) {
        this.loadUri = loadUri;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
