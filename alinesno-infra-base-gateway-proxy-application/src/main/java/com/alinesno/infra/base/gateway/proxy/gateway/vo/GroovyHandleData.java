package com.alinesno.infra.base.gateway.proxy.gateway.vo;

import java.util.Map;

import lombok.Data;

/**
 * @Description
 * @Author JL
 * @Date 2022/3/11
 * @Version V1.0
 */
@Data
public class GroovyHandleData {

    private Map<String, String> paramMap;
    private String body;

    public GroovyHandleData(Map<String, String> paramMap, String body){
        this.paramMap = paramMap;
        this.body = body;
    }

}
