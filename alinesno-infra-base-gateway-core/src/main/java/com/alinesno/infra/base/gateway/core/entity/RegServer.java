package com.alinesno.infra.base.gateway.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description 客户端注册网关路由服务实体类
 * @author jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
@Entity
@Table(name="regserver")
@Data
public class RegServer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "客户端ID不能为空")
    @Size(min = 2, max = 40, message = "客户端ID长度必需在2到40个字符内")
    @Column(name = "clientId" )
    private String clientId;
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    @Column(name = "routeId")
    private String routeId;
    /**
     * token加密内容
     */
    @Column(name = "token")
    private String token;
    /**
     * token加密密钥
     */
    @Column(name = "secretKey")
    private String secretKey;
    /**
     * token有效期截止时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "tokenEffectiveTime")
    private Date tokenEffectiveTime;
    /**
     * 状态，0是启用，1是禁用
     */
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
}
