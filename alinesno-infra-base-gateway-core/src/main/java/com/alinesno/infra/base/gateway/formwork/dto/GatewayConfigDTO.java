package com.alinesno.infra.base.gateway.formwork.dto;


/**
 * @author luoxiaodong
 * @data 2023/04/04 11:55
 */
public class GatewayConfigDTO {

    /**
     * 负载均衡ID
     */
    private String balancedId;
    /**
     * 网关路由ID
     */
    private String routeId;
    /**
     * 客户端注册网关路由的关联表ID
     */
    private Long regServerId;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端IP
     */
//    private String clientIp;
    /**
     * 黑|白名单IP
     */
    private String ip;
    /**
     * groovyScript规则引擎动态脚本ID
     */
    private Long groovyScriptId;
    /**
     * 创建时间戳
     */
    private Long createTime;
    /**
     * 操作用户token
     */
    private String operatorToken;

    public String getBalancedId() {
        return balancedId;
    }

    public void setBalancedId(String balancedId) {
        this.balancedId = balancedId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Long getRegServerId() {
        return regServerId;
    }

    public void setRegServerId(Long regServerId) {
        this.regServerId = regServerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getGroovyScriptId() {
        return groovyScriptId;
    }

    public void setGroovyScriptId(Long groovyScriptId) {
        this.groovyScriptId = groovyScriptId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getOperatorToken() {
        return operatorToken;
    }

    public void setOperatorToken(String operatorToken) {
        this.operatorToken = operatorToken;
    }
}
