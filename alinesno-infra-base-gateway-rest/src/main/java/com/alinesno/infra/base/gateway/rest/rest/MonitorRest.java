package com.alinesno.infra.base.gateway.rest.rest;

import com.alinesno.infra.base.gateway.core.base.BaseRest;
import com.alinesno.infra.base.gateway.facade.bean.MonitorReq;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.dto.ApiResult;
import com.alinesno.infra.base.gateway.facade.entity.Monitor;
import com.alinesno.infra.base.gateway.domain.service.MonitorService;
import com.alinesno.infra.base.gateway.rest.aop.DataFilter;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @description 接口监控
 * @author  JL
 * @date 2021/04/14
 * @version v1.0.0
 */
@RestController
@RequestMapping("/monitor")
public class MonitorRest extends BaseRest {

    @Resource
    private MonitorService monitorService;

    @DataFilter
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult list(@RequestBody MonitorReq monitorReq){
        return new ApiResult(monitorService.list(monitorReq));
    }

    /**
     * 关闭本次告警，状态置为0正常
     * @param id
     * @return
     */
    @RequestMapping(value = "/close", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult close(@RequestParam String id){
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Monitor monitor = monitorService.findById(id);
        Assert.notNull(monitor, "未获取到对象");
        monitor.setStatus(Constants.YES);
        monitorService.update(monitor);
        return new ApiResult();
    }

}
