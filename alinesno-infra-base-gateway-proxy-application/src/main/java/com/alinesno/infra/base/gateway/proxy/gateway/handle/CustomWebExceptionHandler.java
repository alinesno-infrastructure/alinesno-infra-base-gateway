package com.alinesno.infra.base.gateway.proxy.gateway.handle;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.gateway.core.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description 自定义网关路由转发异常处理器
 * @author  JL
 * @date 2021/11/05
 * @version 1.0.0
 */
@Slf4j
@Order(-1)
@Component
public class CustomWebExceptionHandler implements WebExceptionHandler {

    private static final Set<String> DISCONNECTED_CLIENT_EXCEPTIONS;

    /**
     * 排除部份异常不做自定义信息包装;
     */
    static {
        Set<String> exceptions = new HashSet<>();
        exceptions.add("AbortedException");
        exceptions.add("ClientAbortException");
        exceptions.add("EOFException");
        exceptions.add("EofException");
        DISCONNECTED_CLIENT_EXCEPTIONS = Collections.unmodifiableSet(exceptions);
    }

    /**
     * 执行入口
     * @param exchange
     * @param ex
     * @return
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted() || isDisconnectedClientError(ex)) {
            return Mono.error(ex);
        }else {
            log.error("服务异常!", ex);
        }
        ServerHttpRequest request = exchange.getRequest();
        String rawQuery = request.getURI().getRawQuery();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        String path = request.getPath() + query ;
        String message ;
        HttpStatus status = determineStatus(ex);
        if (status == null){
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // 通过状态码自定义异常信息
        if (status.value() >= HttpStatus.BAD_REQUEST.value()
                && status.value() < HttpStatus.INTERNAL_SERVER_ERROR.value()){
            message = "路由服务不可达或禁止访问！";
        }else {
            message = "路由服务异常！";
        }
        message += " path：" + path;
        String jsonMsg = JSONObject.toJSONString(new ApiResult(Constants.FAILED, message, null));
        return HttpResponseUtils.write(exchange.getResponse(), status, jsonMsg);
    }

    /**
     * 获取异常中的状态对象
     * @param ex
     * @return
     */
    @Nullable
    protected HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
//            return ((ResponseStatusException) ex).getStatus();
        }
        return null;
    }

    /**
     * 是否为排除异常
     * @param ex
     * @return
     */
    private boolean isDisconnectedClientError(Throwable ex) {
        return DISCONNECTED_CLIENT_EXCEPTIONS.contains(ex.getClass().getSimpleName())
                || isDisconnectedClientErrorMessage(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
    }

    /**
     * 是否为已断开连接的客户端异常
     * @param message
     * @return
     */
    private boolean isDisconnectedClientErrorMessage(String message) {
        message = (message != null) ? message.toLowerCase() : "";
        return (message.contains("broken pipe") || message.contains("connection reset by peer"));
    }
}
