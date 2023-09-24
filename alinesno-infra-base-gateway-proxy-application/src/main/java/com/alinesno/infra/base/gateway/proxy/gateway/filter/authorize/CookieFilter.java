package com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 验证当前请求是否有带指定cookie
 * @Author jianglong
 * @Date 2020/05/25
 * @Version V1.0
 */
@Slf4j
public class CookieFilter extends FilterHandler {

    public CookieFilter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        //header,ip,parm,time,cookie
        if (route.getFilterAuthorizeName().contains("cookie")){
            log.info("处理网关路由请求{},执行cookie过滤 ", route.getId());
            String accessCookie = route.getAccessCookie();
            Assert.isTrue(StringUtils.isNotBlank(accessCookie),"自定义cookie不能为空");
            Assert.isTrue(accessCookie.contains("="),"自定义 cookie 格式错误");
            String[] datas = accessCookie.split("=");
            Assert.notNull(datas[0],"自定义 cookie 名称不能为空");
            Assert.isTrue(datas.length>1,"自定义 cookie 格式错误");
            HttpCookie cookie = request.getCookies().getFirst(datas[0]);
            if (cookie == null){
                throw new IllegalStateException("执行cookie过滤,自定义cookie验证不通过! 请求源cookie为空");
            }
            String value = cookie.getValue();
            if (value != null && datas[1] != null && datas[1].equals(value)){
            }else {
                throw new IllegalStateException("执行cookie过滤,自定义cookie验证失败! 请求源"+datas[0]+"="+value+",自定义目标"+datas[0]+"=" + datas[1]);
            }
        }
    }

}
