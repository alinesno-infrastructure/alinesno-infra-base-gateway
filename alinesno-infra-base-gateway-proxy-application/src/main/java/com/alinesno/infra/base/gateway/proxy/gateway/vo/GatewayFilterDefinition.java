package com.alinesno.infra.base.gateway.proxy.gateway.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description 创建过滤器模型
 * @author  jianglong
 * @date 2020/05/11
 * @version 1.0.0
 */
@Data
public class GatewayFilterDefinition {
    /**
     * Filter Name
     */
    private String name;
    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}
