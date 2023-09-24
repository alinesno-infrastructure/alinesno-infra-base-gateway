package com.alinesno.infra.base.gateway.formwork.consumer;

import com.alinesno.infra.base.gateway.formwork.dto.GatewayConfigDTO;
import com.alinesno.infra.base.gateway.formwork.util.ApiResult;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;

/**
 * @author luoxiaodong
 * @data 2023/04/04 14:26
 */
@BaseRequest(baseURL = "${gatewayProxyUrl}")
public interface GatewayConfigConsumer {
    @Post("/gateway/configuration/change")
    ApiResult change(@JSONBody GatewayConfigDTO entity);
}
