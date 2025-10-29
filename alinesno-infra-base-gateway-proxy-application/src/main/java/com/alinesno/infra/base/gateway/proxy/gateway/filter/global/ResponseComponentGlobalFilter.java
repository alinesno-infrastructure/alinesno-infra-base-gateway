package com.alinesno.infra.base.gateway.proxy.gateway.filter.global;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.gateway.core.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.dto.ApiResult;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteGroovyCache;
import com.alinesno.infra.base.gateway.proxy.gateway.service.DynamicGroovyService;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GroovyHandleData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @description 自定义组件全局过滤器，对所有响应触发GroovyScript规则引擎动态脚本较验
 * @author  JL
 * @date 2022/3/16
 * @version 1.0.0
 */
@Slf4j
@Component
public class ResponseComponentGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private DynamicGroovyService dynamicGroovyService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        //判断Groovy缓存中的指定路由ID是否存在
        if (!RouteGroovyCache.containsKey(route.getId())){
            return chain.filter(exchange);
        }
        return chain.filter(exchange.mutate()
                .response(getServerHttpResponseDecorator(exchange, route.getId()))
                .build());
    }

    /**
     * 对response响应数据流重新包装，返回新的ServerHttpResponse对象
     * @param exchange
     * @param routeId
     * @return
     */
    public ServerHttpResponse getServerHttpResponseDecorator(ServerWebExchange exchange, String routeId) {
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = NetworkIpUtils.getIpAddress(request);
        // 获取请求参数
        Map<String,String> paramMap = request.getQueryParams().toSingleValueMap();

        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> responseBody) {
                //只有正常响应才进入到groovy执行链路中
                if (HttpStatus.OK.equals(getStatusCode())) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(responseBody);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        String body = getContent(dataBuffers);
                        // 封装请求参数，用于groovy规则引擎动态脚本中执行
                        GroovyHandleData handleData = new GroovyHandleData(paramMap, body);
                        try {
                            handleData = dynamicGroovyService.responseHandle(exchange, handleData);
                            body = handleData.getBody();
                            log.info("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本组件，返回内容：\n{}", clientIp, routeId, body);
                        } catch (InvocationTargetException e) {
                            log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本反射组件异常：", clientIp, routeId, e);
                            body = getErrMsg(clientIp, routeId, e.getTargetException().getMessage());
                        } catch (Exception e) {
                            log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎组件异常：", clientIp, routeId, e);
                            body = getErrMsg(clientIp, routeId, e.getMessage());
                        }
                        byte[] bodyBytes = StringUtils.isBlank(body) ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
                        // 重新计算内容长度,否则长度与内容不匹配会被浏览器、客户端不显示或显示不完整
                        getHeaders().setContentLength(bodyBytes.length);
                        return bufferFactory().wrap(bodyBytes);
                    }));
                }
                return super.writeWith(responseBody);
            }
        };
    }

    /**
     * 从dataBuffer缓冲区中获取response响应的body内容（注意：基于netty下所有的响应数据会写入到dataBuffer中）
     * @param dataBuffers
     * @return
     */
    private String getContent(List<? extends DataBuffer> dataBuffers){
        if (CollectionUtils.isEmpty(dataBuffers)){
            return null;
        }
        StringBuffer bodyStr = new StringBuffer();
        dataBuffers.forEach(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            String toStr = new String(bytes, StandardCharsets.UTF_8);
            bodyStr.append(toStr);
        });
        return StringUtils.isNotBlank(bodyStr) ? bodyStr.toString() : null;
    }

    /**
     * 包装异常消息
     * @param clientIp
     * @param routeId
     * @param errMsg
     * @return
     */
    private String getErrMsg(String clientIp, String routeId, String errMsg){
        String message= String.format("网关转发客户端【%s】路由请求【%s】，执行组件异常：%s", clientIp, routeId, errMsg);
        return JSONObject.toJSONString(new ApiResult(Constants.FAILED, message, null));
    }

    @Override
    public int getOrder() {
        // 注意一定要设置成负数，保证优先级别高，否则无法触发
        return -2;
    }
}
