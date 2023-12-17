package com.alinesno.infra.base.gateway.core.consumer;

import com.alinesno.infra.base.gateway.core.dto.GatewayConfigDTO;
import com.alinesno.infra.base.gateway.core.util.ApiResult;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;

/**
 * @author luoxiaodong
 * @data 2023/04/04 14:26
 */
@BaseRequest(baseURL = "#{alinesno.infra.base.gateway.proxy.host}")
public interface GatewayConfigConsumer {

    @Post("/api/base/gateway/proxy/configuration/change")
    ApiResult change(@JSONBody GatewayConfigDTO entity);

}
