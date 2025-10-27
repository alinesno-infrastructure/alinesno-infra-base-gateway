package com.alinesno.infra.base.gateway.core.bean;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @description nacos中的配置属性
 * @author JL
 * @date 2021/09/17
 * @version v1.0.0
 */
@Data
@ToString
public class GatewayNacosConfigBean implements java.io.Serializable {

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
     * 生成nacos网关配置
     * @return
     */
    public String getGatewayConfig(){
        String str = "\"createTime\":" + System.currentTimeMillis()/1000;
        if (StringUtils.isNotBlank(balancedId)){
            str += ",\"balancedId\":\"" + balancedId + "\"";
        }
        if (StringUtils.isNotBlank(routeId)){
            str += ",\"routeId\":\"" + routeId + "\"";
        }
        if (regServerId != null){
            str += ",\"regServerId\":" + regServerId ;
        }
        if (StringUtils.isNotBlank(clientId)){
            str += ",\"clientId\":\"" + clientId + "\"";
        }
//        if (StringUtils.isNotBlank(clientIp)){
//            str += ",\"clientIp\":\"" + clientIp + "\"";
//        }
        if (StringUtils.isNotBlank(ip)){
            str += ",\"ip\":\"" + ip + "\"";
        }
        if (groovyScriptId != null){
            str += ",\"groovyScriptId\":" + groovyScriptId ;
        }
        return "gateway={" + str + "}";
    }

}
