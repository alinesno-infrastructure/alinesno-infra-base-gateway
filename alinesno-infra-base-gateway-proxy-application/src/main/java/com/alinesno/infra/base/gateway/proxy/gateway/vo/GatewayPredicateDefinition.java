package com.alinesno.infra.base.gateway.proxy.gateway.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description 路由断言模型
 * @author  jianglong
 * @date 2020/05/11
 * @version 1.0.0
 */
@Data
public class GatewayPredicateDefinition {
    /**
     * 断言对应的Name
     */
    private String name;
    /**
     * 配置的断言规则
     */
    private Map<String, String> args = new LinkedHashMap<>();

}
