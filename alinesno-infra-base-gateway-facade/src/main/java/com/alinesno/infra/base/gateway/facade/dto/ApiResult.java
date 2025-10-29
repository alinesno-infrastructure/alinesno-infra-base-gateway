package com.alinesno.infra.base.gateway.facade.dto;

import com.alinesno.infra.base.gateway.facade.constant.Constants;
import lombok.Data;

import java.util.Date;

/**
 * @description 所有接口调用返回的统一包装结果类
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
@Data
public class ApiResult implements java.io.Serializable {

    private int code;
    private Date timestamp;
    private String msg = "";
    private Object data;

    public ApiResult(){
        this.code = Integer.parseInt(Constants.SUCCESS) ;
    }

    public ApiResult(final String code){
        this.code = Integer.parseInt(code) ; 
    }

    public ApiResult(final String code, final Object data){
        this.code =  Integer.parseInt(code);
        this.data = data;
    }

    public ApiResult(final String code, final String msg, final Object data){
        this.code =  Integer.parseInt(code);
        this.msg = msg;
        this.data = data;
    }

    public ApiResult(final Object data){
        this.code = Integer.parseInt(Constants.SUCCESS) ;
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp == null ? new Date(): timestamp;
    }
}
