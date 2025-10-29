package com.alinesno.infra.base.gateway.adapter.consumer;

import cn.dev33.satoken.stp.StpUtil;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * Forest拦截器
 */
@Slf4j
public class SaTokenForestInterceptor<T> implements Interceptor<T> {

    /**
     * 该方法在请求发送之前被调用, 若返回false则不会继续发送请求
     * @Param request Forest请求对象
     */
    @Override
    public boolean beforeExecute(ForestRequest req) {

        try{
            if(StpUtil.isLogin() && StpUtil.getTokenValue() != null){
                req.addHeader(StpUtil.getTokenName(), "Bearer " + StpUtil.getTokenValue());
            }
        }catch(Exception e){
            log.warn("SaTokenForestInterceptor获取Token失败:{}" , e.getMessage());
        }

        return true;  // 继续执行请求返回true
    }

}
