package com.alinesno.infra.base.gateway.formwork.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description 网关路由实体类
 * @author jianglong
 * @date 2020/05/11
 * @version v1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="route")
@Data
public class Route extends BaseEntity implements java.io.Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "网关路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由ID长度必需在2到40个字符内")
    private String id;

    @NotNull(message = "配置路由ID不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "routeId" )
    private String routeId;

    @NotNull(message = "用户标识不能为空")
    @Size(min = 2, max = 40, message = "用户标识长度必需在2到40个字符内")
    @Column(name = "accountToken" )
    private String accountToken;

    @NotNull(message = "网关路由名称不能为空")
    @Size(min = 2, max = 40, message = "网关路由名称长度必需在2到40个字符内")
    @Column(name = "name" )
    private String name;
    @NotNull(message = "网关路由系统代号不能为空")
    @Size(min = 2, max = 40, message = "网关路由系统代号长度必需在2到40个字符内")
    @Column(name = "systemCode" )
    private String systemCode;
    @NotNull(message = "网关路由客户端分组不能为空")
    @Column(name = "groupCode")
    private String groupCode;
    @NotNull(message = "网关路由服务uri不能为空")
    @Size(min = 2, max = 200, message = "网关路由服务uri长度必需在2到200个字符内")
    @Column(name = "uri")
    private String uri;
    @NotNull(message = "网关路由断言Path不能为空")
    @Size(min = 2, max = 100, message = "网关路由断言Path长度必需在2到100个字符内")
    @Column(name = "path")
    private String path;
    /**
     * 请求类型：POST，GET，DELETE，PUT
     */
    @Column(name = "method")
    private String method;
    /**
     * 断言Hosts
     */
    @Column(name = "host")
    private String host;
    /**
     * 断言RemoteAddrs
     */
    @Column(name = "remoteAddr")
    private String remoteAddr;
    /**
     * 断言Headers
     */
    @Column(name = "header")
    private String header;
    /**
     * 鉴权过滤器类型：ip,token,id
     */
    @Column(name = "filterGatewaName")
    private String filterGatewaName;
    /**
     * 熔断器名称：hystrix,custom
     */
    @Column(name = "filterHystrixName")
    private String filterHystrixName;
    /**
     * 限流器类型：ip,uri,requestId
     */
    @Column(name = "filterRateLimiterName")
    private String filterRateLimiterName;
    /**
     * 过滤器类型：header,ip,param,time,cookie
     */
    @Column(name = "filterAuthorizeName")
    private String filterAuthorizeName;
    /**
     * 回滚消息
     */
    @Column(name = "fallbackMsg")
    private String fallbackMsg;
    /**
     * 回滚超时时长
     */
    @Column(name = "fallbackTimeout")
    private Long fallbackTimeout;
    /**
     * 每1秒限制请求数(令牌数)
     */
    @Column(name = "replenishRate")
    private Integer replenishRate;
    /**
     * 令牌桶的容量
     */
    @Column(name = "burstCapacity")
    private Integer burstCapacity;
    @Transient
    private String weightName;
    @Transient
    private Integer weight;
    /**
     * 状态，0是启用，1是禁用
     */
    @Column(name = "status")
    private String status;
    /**
     * 断言前缀截取层数
     */
    @Column(name = "stripPrefix")
    private Integer stripPrefix;
    /**
     * 请求参数
     */
    @Column(name = "requestParameter")
    private String requestParameter;
    /**
     * 重写Path路径
     */
    @Column(name = "rewritePath")
    private String rewritePath ;
    /**
     * 鉴权：Header
     */
    @Column(name = "accessHeader")
    private String accessHeader;
    /**
     * 鉴权：Header
     */
    @Column(name = "accessIp")
    private String accessIp;
    /**
     * 鉴权：Parameter
     */
    @Column(name = "accessParameter")
    private String accessParameter;
    /**
     * 鉴权：Time
     */
    @Column(name = "accessTime")
    private String accessTime;
    /**
     * 鉴权：Cookie
     */
    @Column(name = "accessCookie")
    private String accessCookie;
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
