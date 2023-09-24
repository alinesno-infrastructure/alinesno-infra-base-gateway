package com.alinesno.infra.base.gateway.formwork.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @description 网关路由API接口文档实体类
 * @author JL
 * @date 2020/11/25
 * @version v1.0.0
 */
@Entity
@Table(name="apidoc")
@Data
public class ApiDoc {

    /**
     * 主键，同route_id
     */
    @Id
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 1, max = 40, message = "网关路由ID长度必需在1到40个字符内")
    private String id;
    /**
     * 内容
     */
    @Column(name = "content" )
    private String content;

}
