package com.alinesno.infra.base.gateway.core.util;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.dto.ApiResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @description 使用给定的Publisher者将消息正文写入底层HTTP层.
 * @author jianglong
 * @date 2020/05/19
 * @version v1.0.0
 */
public class HttpResponseUtils {

    /**
     * 正常输出
     * @param response
     * @param msg
     */
    public static Mono<Void> writeOk(ServerHttpResponse response, String msg) {
        String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.SUCCESS, msg, null));
        return write(response, HttpStatus.OK, jsonMsg);
    }

    /**
     * 未授权输出
     * @param response
     * @param msg
     */
    public static Mono<Void> writeUnauth(ServerHttpResponse response, String msg) {
        String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.FAILED, msg, null));
        return write(response, HttpStatus.UNAUTHORIZED, jsonMsg);
    }

    /**
     * 内部服务错误输出
     * @param response
     * @param msg
     */
    public static Mono<Void> writeError(ServerHttpResponse response, String msg) {
        String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.FAILED, msg, null));
        return write(response, HttpStatus.INTERNAL_SERVER_ERROR, jsonMsg);
    }

    /**
     * 自定义输出
     * @param response
     * @param statusCode
     * @param msg
     */
    public static Mono<Void> write(ServerHttpResponse response, HttpStatus statusCode, String msg){
        msg = msg == null ? Constants.NULL : msg;
        response.setStatusCode(statusCode);
        response.getHeaders().add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
