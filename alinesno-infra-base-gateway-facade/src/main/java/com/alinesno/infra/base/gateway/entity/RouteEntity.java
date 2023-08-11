package com.alinesno.infra.base.gateway.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 路由实体类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@TableName("route")
public class RouteEntity extends InfraBaseEntity {

    /**
     * 路由ID
     */
    @TableField("route_id")
    private String routeId;

    /**
     * 用户标识
     */
    @TableField("account_token")
    private String accountToken;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 系统编码
     */
    @TableField("system_code")
    private String systemCode;

    /**
     * 分组编码
     */
    @TableField("group_code")
    private String groupCode;

    /**
     * URI
     */
    @TableField("uri")
    private String uri;

    /**
     * 路径
     */
    @TableField("path")
    private String path;

    /**
     * 请求类型：POST，GET，DELETE，PUT
     */
    @TableField("method")
    private String method;

    /**
     * 断言Hosts
     */
    @TableField("host")
    private String host;

    /**
     * 断言RemoteAddrs
     */
    @TableField("remote_addr")
    private String remoteAddr;

    /**
     * 断言Headers
     */
    @TableField("header")
    private String header;

    /**
     * 鉴权过滤器类型：ip,token,id
     */
    @TableField("filter_gateway_name")
    private String filterGatewaName;

    /**
     * 熔断器名称：hystrix,custom
     */
    @TableField("filter_hystrix_name")
    private String filterHystrixName;

    /**
     * 限流器类型：ip,uri,requestId
     */
    @TableField("filter_rate_limiter_name")
    private String filterRateLimiterName;

    /**
     * 过滤器类型：header,ip,param,time,cookie
     */
    @TableField("filter_authorize_name")
    private String filterAuthorizeName;

    /**
     * 回滚消息
     */
    @TableField("fallback_msg")
    private String fallbackMsg;

    /**
     * 回滚超时时长
     */
    @TableField("fallback_timeout")
    private Long fallbackTimeout;

    /**
     * 每1秒限制请求数(令牌数)
     */
    @TableField("replenish_rate")
    private Integer replenishRate;

    /**
     * 令牌桶的容量
     */
    @TableField("burst_capacity")
    private Integer burstCapacity;

    /**
     * 权重名称
     */
    @TableField("weight_name")
    private String weightName;

    /**
     * 权重
     */
    @TableField("weight")
    private Integer weight;

    /**
     * 状态，0是启用，1是禁用
     */
    @TableField("status")
    private String status;

    /**
     * 断言前缀截取层数
     */
    @TableField("strip_prefix")
    private Integer stripPrefix;

    /**
     * 请求参数
     */
    @TableField("request_parameter")
    private String requestParameter;

    /**
     * 重写Path路径
     */
    @TableField("rewrite_path")
    private String rewritePath;

    /**
     * 鉴权：Header
     */
    @TableField("access_header")
    private String accessHeader;

    /**
     * 鉴权：IP
     */
    @TableField("access_ip")
    private String accessIp;

    /**
     * 鉴权：Parameter
     */
    @TableField("access_parameter")
    private String accessParameter;

    /**
     * 鉴权：Time
     */
    @TableField("access_time")
    private String accessTime;

    /**
     * 鉴权：Cookie
     */
    @TableField("access_cookie")
    private String accessCookie;

    // 省略getter和setter方法

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFilterGatewaName() {
        return filterGatewaName;
    }

    public void setFilterGatewaName(String filterGatewaName) {
        this.filterGatewaName = filterGatewaName;
    }

    public String getFilterHystrixName() {
        return filterHystrixName;
    }

    public void setFilterHystrixName(String filterHystrixName) {
        this.filterHystrixName = filterHystrixName;
    }

    public String getFilterRateLimiterName() {
        return filterRateLimiterName;
    }

    public void setFilterRateLimiterName(String filterRateLimiterName) {
        this.filterRateLimiterName = filterRateLimiterName;
    }

    public String getFilterAuthorizeName() {
        return filterAuthorizeName;
    }

    public void setFilterAuthorizeName(String filterAuthorizeName) {
        this.filterAuthorizeName = filterAuthorizeName;
    }

    public String getFallbackMsg() {
        return fallbackMsg;
    }

    public void setFallbackMsg(String fallbackMsg) {
        this.fallbackMsg = fallbackMsg;
    }

    public Long getFallbackTimeout() {
        return fallbackTimeout;
    }

    public void setFallbackTimeout(Long fallbackTimeout) {
        this.fallbackTimeout = fallbackTimeout;
    }

    public Integer getReplenishRate() {
        return replenishRate;
    }

    public void setReplenishRate(Integer replenishRate) {
        this.replenishRate = replenishRate;
    }

    public Integer getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(Integer burstCapacity) {
        this.burstCapacity = burstCapacity;
    }

    public String getWeightName() {
        return weightName;
    }

    public void setWeightName(String weightName) {
        this.weightName = weightName;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Integer stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public String getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(String requestParameter) {
        this.requestParameter = requestParameter;
    }

    public String getRewritePath() {
        return rewritePath;
    }

    public void setRewritePath(String rewritePath) {
        this.rewritePath = rewritePath;
    }

    public String getAccessHeader() {
        return accessHeader;
    }

    public void setAccessHeader(String accessHeader) {
        this.accessHeader = accessHeader;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public void setAccessIp(String accessIp) {
        this.accessIp = accessIp;
    }

    public String getAccessParameter() {
        return accessParameter;
    }

    public void setAccessParameter(String accessParameter) {
        this.accessParameter = accessParameter;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getAccessCookie() {
        return accessCookie;
    }

    public void setAccessCookie(String accessCookie) {
        this.accessCookie = accessCookie;
    }
}