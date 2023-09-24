package com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 对请求中带的指定参数进行验证
 * @Author jianglong
 * @Date 2020/05/25
 * @Version V1.0
 */
@Slf4j
public class ParameterFilter extends FilterHandler {

    public ParameterFilter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        //header,ip,parameter,time,cookie
        if (route.getFilterAuthorizeName().contains("parameter")){
            log.info("处理网关路由请求{},执行parameter过滤 ", route.getId());
            String accessParameter = route.getAccessParameter();
            Assert.isTrue(StringUtils.isNotBlank(accessParameter),"自定义参数不能为空");
            Assert.isTrue(accessParameter.contains("="),"自定义参数格式错误");
            List<String[]> dataList =  new ArrayList<>();
            if (accessParameter.contains("&")){
                String [] parameters = accessParameter.split("&");
                for (String parameter : parameters){
                    dataList.add(splitData(parameter));
                }
            }else {
                dataList.add(splitData(accessParameter));
            }
            for (String [] datas : dataList){
                String value = request.getQueryParams().getFirst(datas[0]);
                if (datas[1] != null && value != null && datas[1].equals(value)){
                }else {
                    throw new IllegalStateException("执行parameter过滤,自定义 parameter 验证不通过! 请求源"+datas[0]+"="+value+",自定义目标"+datas[0]+"=" + datas[1]);
                }
            }
        }
    }

    private String [] splitData(String parameter){
        if (parameter.indexOf("=") != -1){
            String [] datas = parameter.split("=");
            return datas.length>1?datas:null;
        }
        return null;
    }

}
