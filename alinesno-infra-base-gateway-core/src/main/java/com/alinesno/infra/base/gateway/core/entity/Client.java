package com.alinesno.infra.base.gateway.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description 客户端实体类
 * @author jianglong
 * @date 2020/05/15
 * @version v1.0.0
 */
@Entity
@Table(name="client")
@Data
public class Client extends BaseEntity implements java.io.Serializable {
    @Id
    private String id;
    @NotNull(message = "客户端系统代号不能为空")
    @Size(min = 2, max = 40, message = "网关路由系统代号长度必需在2到40个字符内")
    @Column(name = "systemCode" )
    private String systemCode;
    @NotNull(message = "客户端名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name")
    private String name;
    @NotNull(message = "客户端分组不能为空")
    @Column(name = "groupCode")
    private String groupCode;
    @Column(name = "ip")
    private String ip;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    @Column(name = "remarks")
    private String remarks;
    /**
     * 创建时间和修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "创建时间不能为空")
    @Column(name = "createTime")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updateTime")
    private Date updateTime;
}
