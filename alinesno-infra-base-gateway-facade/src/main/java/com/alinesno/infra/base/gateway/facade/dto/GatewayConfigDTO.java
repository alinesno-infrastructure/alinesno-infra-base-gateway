package com.alinesno.infra.base.gateway.facade.dto;


import lombok.Data;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Data
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

}
