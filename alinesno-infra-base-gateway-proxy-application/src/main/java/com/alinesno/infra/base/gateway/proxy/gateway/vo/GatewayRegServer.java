package com.alinesno.infra.base.gateway.proxy.gateway.vo;

import lombok.Data;

/**
 * @description
 * @author  JL
 * @date 2021/09/30
 * @version 1.0.0
 */
@Data
public class GatewayRegServer implements java.io.Serializable {
    private String clientId;
    private String token;
    private String ip;
    private String secretKey;
}
