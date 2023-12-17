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
 * @description IP实体类
 * @author jianglong
 * @date 2020/05/28
 * @version v1.0.0
 */
@Entity
@Table(name="secureip")
@Data
public class SecureIp extends BaseEntity implements java.io.Serializable {

    @Id
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "IP的ID长度必需在2到40个字符内")
    private String id;

    @NotNull(message = "IP名称不能为空")
    @Size(min = 4, max = 16, message = "IP名称字段长度必需在2到40个字符内")
    private String ip;

    @NotNull(message = "用户标识不能为空")
    @Size(min = 2, max = 40, message = "用户标识长度必需在2到40个字符内")
    @Column(name = "accountToken" )
    private String accountToken;

    /**
     * 状态，0是启用，1是禁用
     */
    @NotNull(message = "IP状态不能为空")
    @Size(min = 1, max = 2, message = "状态字段长度必需在1个字符")
    @Column(name = "status")
    private String status;
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
    @Column(name = "remarks")
    private String remarks;

}
