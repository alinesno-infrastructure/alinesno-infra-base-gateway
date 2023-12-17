package com.alinesno.infra.base.gateway.core.util;

/**
 *
 */
public enum HttpEnum {
    /**
     * request请求
     */
    HTTP_REQUEST("request"),
    /**
     * response响应
     */
    HTTP_RESPONSE("response");

    String value;

    HttpEnum( String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
