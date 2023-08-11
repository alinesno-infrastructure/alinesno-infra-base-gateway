package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * API文档实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("api_doc")
public class ApiDocEntity extends InfraBaseEntity {

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    // 省略getter和setter方法

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}