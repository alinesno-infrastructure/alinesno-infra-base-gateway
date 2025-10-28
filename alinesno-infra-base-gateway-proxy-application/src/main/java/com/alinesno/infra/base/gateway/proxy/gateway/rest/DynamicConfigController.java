package com.alinesno.infra.base.gateway.proxy.gateway.rest;

import com.alinesno.infra.base.gateway.adapter.dto.ApiResult;
import com.alinesno.infra.base.gateway.adapter.dto.GatewayConfigDTO;
import com.alinesno.infra.base.gateway.proxy.gateway.service.ConfigRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author  luoxiaodong
 * @version 1.0.0
 */
@CrossOrigin( allowCredentials = "false")
@RestController
@RequestMapping("/api/base/gateway/proxy/configuration")
public class DynamicConfigController {

    @Autowired
    ConfigRefreshService configRefreshService ;

    /**
     * 配置变更
     * @param
     * @return
     */
    @PostMapping("/change")
    public ApiResult change(@RequestBody GatewayConfigDTO gatewayConfig) {
        configRefreshService.setGatewayConfig(gatewayConfig);
        return new ApiResult();
    }
}
