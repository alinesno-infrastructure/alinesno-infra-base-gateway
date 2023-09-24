package com.alinesno.infra.base.gateway.proxy.gateway.vo;

import lombok.Data;

/**
 * @Description
 * @Author JL
 * @Date 2021/09/30
 * @Version V1.0
 */
@Data
public class GatewayRegServer implements java.io.Serializable {
    private String clientId;
    private String token;
    private String ip;
    private String secretKey;
}
