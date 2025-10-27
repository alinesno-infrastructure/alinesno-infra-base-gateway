package com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @description 验证当前请求是否有带指定heander头部信息
 * @author  jianglong
 * @date 2020/05/25
 * @version 1.0.0
 */
@Slf4j
public class HeaderFilter extends FilterHandler {

    public HeaderFilter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        //header,ip,parameter,time,cookie
        if (route.getFilterAuthorizeName().contains("header")){
            log.info("处理网关路由请求{},执行header过滤 ", route.getId());
            String accessHeader = route.getAccessHeader();
            Assert.isTrue(StringUtils.isNotBlank(accessHeader),"自定义header不能为空");
            Assert.isTrue(accessHeader.contains(":"),"自定义header格式错误");
            String[] datas = Arrays.stream(accessHeader.split(":")).map(item -> item.strip()).toArray(String[]::new);
            Assert.notNull(datas[0],"自定义 header 名称不能为空");
            Assert.isTrue(datas.length>1,"自定义 header 格式错误");
            HttpHeaders headers = request.getHeaders();
            String value = headers.getFirst(datas[0]);
            if (value != null && datas[1] != null && datas[1].equals(value)){
            }else {
                throw new IllegalStateException("执行header过滤,自定义 header 验证不通过! 请求源" +datas[0]+ "=" +value+ ",自定义目标" +datas[0]+ "=" + datas[1]);
            }
        }
    }

}
