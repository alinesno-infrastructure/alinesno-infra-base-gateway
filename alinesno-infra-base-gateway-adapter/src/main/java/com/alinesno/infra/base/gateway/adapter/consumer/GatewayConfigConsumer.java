package com.alinesno.infra.base.gateway.adapter.consumer;

import com.alinesno.infra.base.gateway.facade.dto.ApiResult;
import com.alinesno.infra.base.gateway.facade.dto.GatewayConfigDTO;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.base.gateway.proxy.host}" ,
        interceptor = {SaTokenForestInterceptor.class} ,
        connectTimeout = 30*1000
)
public interface GatewayConfigConsumer {

    @Post("/api/base/gateway/proxy/configuration/change")
    ApiResult change(@JSONBody GatewayConfigDTO entity);

}
