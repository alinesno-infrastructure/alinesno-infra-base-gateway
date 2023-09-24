package com.alinesno.infra.base.gateway.formwork.service;

import com.alinesno.infra.base.gateway.formwork.dto.GatewayConfigDTO;
import com.alinesno.infra.base.gateway.formwork.consumer.GatewayConfigConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luoxiaodong
 * @data 2023/04/04 13:59
 */
@Service
public class CustomRestConfigService {

    @Autowired
    GatewayConfigConsumer gatewayConfigConsumer;

    public void publishBalancedConfig(final String balancedId){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setBalancedId(balancedId);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishRouteConfig(final String routeId){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setRouteId(routeId);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishRegServerConfig(final Long regServerId){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setRegServerId(regServerId);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishClientConfig(final String clientId){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setClientId(clientId);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishIpConfig(String operatorToken, final String ip){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setIp(ip);
        gatewayConfigDTO.setOperatorToken(operatorToken);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishGroovyScriptConfig(final Long groovyScriptId){
        GatewayConfigDTO gatewayConfigDTO = new GatewayConfigDTO();
        gatewayConfigDTO.setGroovyScriptId(groovyScriptId);
        publishConfigToProxy(gatewayConfigDTO);
    }


    public void publishConfigToProxy(GatewayConfigDTO gatewayConfig) {
        gatewayConfigConsumer.change(gatewayConfig);
    }


}
