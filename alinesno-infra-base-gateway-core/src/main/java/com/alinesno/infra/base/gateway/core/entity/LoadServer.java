package com.alinesno.infra.base.gateway.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description 负载服务实体类
 * @author jianglong
 * @date 2020/06/28
 * @version v1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="loadserver")
@Data
public class LoadServer extends BaseEntity implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "路由主键ID值不能为空")
    @Column(name = "routeId")
    private String routeId;
    @NotNull(message = "负载ID值不能为空")
    @Column(name = "balancedId")
    private String balancedId;
    @NotNull(message = "网关路由权重值不能为空")
    @Column(name = "weight")
    private Integer weight;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

}
