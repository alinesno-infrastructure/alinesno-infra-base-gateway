package com.alinesno.infra.base.gateway.manage.rest;


import com.alinesno.infra.base.gateway.formwork.base.BaseRest;
import com.alinesno.infra.base.gateway.formwork.bean.LoadServerReq;
import com.alinesno.infra.base.gateway.formwork.entity.LoadServer;
import com.alinesno.infra.base.gateway.formwork.service.LoadServerService;
import com.alinesno.infra.base.gateway.formwork.util.ApiResult;
import com.alinesno.infra.base.gateway.manage.aop.DataFilter;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 负载服务管理控制器
 * @author  JL
 * @date 2020/06/28
 * @version v1.0.0
 */
@RestController
@RequestMapping("/loadServer")
public class LoadServerRest extends BaseRest<LoadServer> {

    @Resource
    private LoadServerService loadServerService;

    /**
     * 查询当前负载网关已加配置路由服务
     * @param loadServerReq
     * @return
     */
    @RequestMapping(value = "/regList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult regList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, true);
    }

    /**
     * 查询当前负载网关未加配置路由服务
     * @param loadServerReq
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/notRegPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult notRegPageList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, false);
    }

    /**
     * 查询数据
     * @param loadServerReq
     * @param isReg
     * @return
     */
    private ApiResult list(LoadServerReq loadServerReq, boolean isReg){
        Assert.notNull(loadServerReq, "未获取到对象");
        if (isReg) {
            Assert.isTrue(StringUtils.isNotBlank(loadServerReq.getBalancedId()), "未获取到对象ID");
            return new ApiResult(loadServerService.loadServerList(loadServerReq.getBalancedId()));
        }else {
            int currentPage = getCurrentPage(loadServerReq.getCurrentPage());
            int pageSize = getPageSize(loadServerReq.getPageSize());
            return new ApiResult(loadServerService.notLoadServerPageList(currentPage, pageSize,  loadServerReq.getOperatorId()));
        }
    }

}
