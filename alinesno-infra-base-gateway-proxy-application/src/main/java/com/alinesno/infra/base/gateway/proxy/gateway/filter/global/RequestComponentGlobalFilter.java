package com.alinesno.infra.base.gateway.proxy.gateway.filter.global;

import com.alinesno.infra.base.gateway.formwork.util.HttpResponseUtils;
import com.alinesno.infra.base.gateway.formwork.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RotueGroovyCache;
import com.alinesno.infra.base.gateway.proxy.gateway.service.DynamicGroovyService;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GroovyHandleData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @Description 自定义组件全局过滤器，对所有请求触发GroovyScript规则引擎动态脚本较验
 * @Author JL
 * @Date 2022/3/16
 * @Version V1.0
 */
@Slf4j
@Component
public class RequestComponentGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private DynamicGroovyService dynamicGroovyService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        //判断Groovy缓存中的指定路由ID是否存在
        if (!RotueGroovyCache.containsKey(route.getId())){
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = request.getHeaders().getContentType();
        //判断是否为可解析内容格式（不支持pdf\image\multipart等文件内容格式）
        if (restrictive(mediaType)){
            return chain.filter(exchange);
        }

        String routeId = route.getId();
        String clientIp = NetworkIpUtils.getIpAddress(request);
        //databuffer管理工厂类
        DataBufferFactory dataBufferFactory =new DefaultDataBufferFactory();
        return DataBufferUtils.join(request.getBody())
                //如果并未设置body值，则默认创建空dataBuffer对象
                .switchIfEmpty(Mono.just(dataBufferFactory.allocateBuffer(1)))
                .flatMap(dataBuffer -> {
                    try {
                        return chain.filter(exchange.mutate()
                                .request(getServerHttpRequestDecorator(exchange, dataBuffer, clientIp, routeId, dataBufferFactory))
                                .build());
                    } catch (InvocationTargetException e) {
                        log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本反射验证异常：", clientIp, routeId, e);
                        return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关转发客户端【" + clientIp + "】路由请求【" +routeId + "】，执行验证异常：" + e.getTargetException().getMessage());
                    } catch (Exception e) {
                        log.error("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎验证异常：", clientIp, routeId, e);
                        return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关转发客户端【" + clientIp + "】路由请求【" + routeId + "】，执行验证异常：" + e.getMessage());
                    }
                });
    }

    /**
     * 创建与获取包装getBody()方法后的ServerHttpRequest对象
     * @param exchange
     * @param dataBuffer
     * @return
     * @throws InvocationTargetException
     * @throws Exception
     */
    public ServerHttpRequest getServerHttpRequestDecorator(ServerWebExchange exchange, DataBuffer dataBuffer, String clientIp,
                                                           String routeId, DataBufferFactory dataBufferFactory ) throws InvocationTargetException, Exception{
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求参数
        Map<String,String> paramMap = request.getQueryParams().toSingleValueMap();
        int hashCode = paramMap.toString().hashCode();
        // 获取请求中的body内容
        String body = this.getBodyContent(dataBuffer);
        // 封装请求参数，用于groovy规则引擎动态脚本中执行
        GroovyHandleData handleData = new GroovyHandleData(paramMap, body);
        // 执行request请求组件
        handleData = dynamicGroovyService.requestHandle(exchange, handleData);
        final String newsBody = handleData.getBody();
        log.info("网关转发客户端【{}】路由请求【{}】，执行Groovy规则引擎动态脚本组件，请求内容：\n{}", clientIp, routeId, newsBody);

        if (hashCode != handleData.getParamMap().toString().hashCode()) {
            // 重写请求参数
            request = replaceRawQuery(request, handleData.getParamMap());
        }
        //封装request，传给下一级
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                if (StringUtils.isNotBlank(newsBody)) {
                    //新建一个databuffer对象，重新写入请求body数据，5为要分配的缓冲区的初始容量
                    DataBuffer buffer = dataBufferFactory.allocateBuffer(DefaultDataBufferFactory.DEFAULT_INITIAL_CAPACITY);
                    buffer.write(newsBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.defer(() -> Flux.just(buffer.slice(0, buffer.readableByteCount())));
                }else {
                    return getDelegate().getBody();
                }
            }
        };
    }
    /**
     * 在请求url的参数中追加新参数
     * @param request
     * @param queryMap
     * @return
     */
    private ServerHttpRequest replaceRawQuery(ServerHttpRequest request, Map<String, String> queryMap){
        if (CollectionUtils.isEmpty(queryMap)){
            return request;
        }
        URI uri = request.getURI();
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : queryMap.entrySet()){
            query.append("&").append(entry.getKey()).append('=').append(entry.getValue());
        }
        request.mutate().uri(uri);
        try {
            URI newUri = UriComponentsBuilder.fromUri(uri).replaceQuery(query.substring(1)).build(true).toUri();
            return request.mutate().uri(newUri).build();
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Invalid URI query: \"" + query.toString() + "\"");
        }
    }

    /**
     * 从dataBuffer缓冲区中获取request请求的body内容（注意：基于netty下所有的请求数据会写入到dataBuffer中）
     * @param dataBuffer
     * @return
     */
    private String getBodyContent(DataBuffer dataBuffer){
        int count = dataBuffer.readableByteCount();
        if (count > 0) {
            byte[] bytes = new byte[count];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 限制部份内容格式进入到groovy规则引擎动态脚本组件中；（不支持pdf\image\multipart等文件内容格式）
     * @param mediaType
     * @return
     */
    private boolean restrictive(MediaType mediaType){
        if (mediaType == MediaType.APPLICATION_PDF) {
            return true;
        } else if (mediaType == MediaType.APPLICATION_OCTET_STREAM){
            return true;
        } else if (mediaType == MediaType.IMAGE_GIF){
            return true;
        } else if (mediaType == MediaType.IMAGE_JPEG){
            return true;
        } else if (mediaType == MediaType.IMAGE_PNG){
            return true;
        } else if (mediaType == MediaType.MULTIPART_FORM_DATA){
            return true;
        } else if (mediaType == MediaType.MULTIPART_MIXED){
            return true;
        } else if (mediaType == MediaType.MULTIPART_RELATED){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getOrder() {
        // 置于所有过滤器之后触发
        return Ordered.LOWEST_PRECEDENCE;
    }
}