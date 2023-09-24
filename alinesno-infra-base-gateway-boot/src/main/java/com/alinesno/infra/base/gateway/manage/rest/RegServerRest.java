package com.alinesno.infra.base.gateway.manage.rest;

import com.alinesno.infra.base.gateway.formwork.base.BaseRest;
import com.alinesno.infra.base.gateway.formwork.bean.RegServerReq;
import com.alinesno.infra.base.gateway.formwork.bean.TokenReq;
import com.alinesno.infra.base.gateway.formwork.entity.RegServer;
import com.alinesno.infra.base.gateway.formwork.service.CustomRestConfigService;
import com.alinesno.infra.base.gateway.formwork.service.RegServerService;
import com.alinesno.infra.base.gateway.formwork.util.ApiResult;
import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.formwork.util.JwtTokenUtils;
import com.alinesno.infra.base.gateway.manage.aop.DataFilter;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @description 客户端注册服务控制器类
 * @author JL
 * @date 2020/05/16
 * @version v1.0.0
 */
@RestController
@RequestMapping("/regServer")
public class RegServerRest extends BaseRest {

    @Resource
    private RegServerService regServerService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomRestConfigService customRestConfigService;

    /**
     * 添加注册到网关路由的客户端服务
     * @param regServer
     * @return
     */
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ApiResult add(@RequestBody RegServer regServer) {
        Assert.notNull(regServer, "未获取到对象");
        //默认禁止通行
        regServer.setStatus(Constants.NO);
        regServer.setCreateTime(new Date());
        this.validate(regServer);
        //验证注册服务是否重复
        RegServer qServer = new RegServer();
        qServer.setClientId(regServer.getClientId());
        qServer.setRouteId(regServer.getRouteId());
        long count = regServerService.count(qServer);
        Assert.isTrue(count <= 0, "客户端已注册该服务，请不要重复注册");
        //保存
        regServerService.save(regServer);
        //this.setClientCacheVersion();
        customRestConfigService.publishRegServerConfig(regServer.getId());
        return new ApiResult();
    }

    /**
     * 删除注册到网关路由的客户端服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult delete(@RequestParam Long id) {
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        regServerService.deleteById(id);
        //this.setClientCacheVersion();
        customRestConfigService.publishRegServerConfig(id);
        return new ApiResult();
    }

    /**
     * 更新注册到网关路由的客户端服务
     * @param regServer
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ApiResult update(@RequestBody RegServer regServer) {
        Assert.notNull(regServer, "未获取到对象");
        regServer.setUpdateTime(new Date());
        this.validate(regServer);
        regServerService.update(regServer);
        //this.setClientCacheVersion();
        customRestConfigService.publishRegServerConfig(regServer.getId());
        return new ApiResult();
    }

    /**
     * 获取注册网关路由客户端服务
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult findById(@RequestParam Long id) {
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        return new ApiResult(regServerService.findById(id));
    }

    /**
     * 获取网关路由服务列表（分页）
     * @param regServerReq
     * @return
     */
    @RequestMapping(value = "/serverPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult serverPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getClientId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setClientId(regServerReq.getClientId());
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return new ApiResult(regServerService.serverPageList(regServer, currentPage, pageSize));
    }

    /**
     * 获取客户端列表（分页）
     * @param regServerReq
     * @return
     */
    @RequestMapping(value = "/clientPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult clientPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setRouteId(regServerReq.getRouteId());
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return new ApiResult(regServerService.clientPageList(regServer, currentPage, pageSize));
    }

    /**
     * 获取已注册网关路由列表
     * @param regServerReq
     * @return
     */
    @RequestMapping(value = "/regClientList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult regClientList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取到对象查询ID");
        RegServer regServer = new RegServer();
        regServer.setRouteId(regServerReq.getRouteId());
        return new ApiResult(regServerService.regClientList(regServer));
    }

    /**
     * 启用注册网关路由下的客户端
     * @param id
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult start(@RequestParam Long id) {
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        RegServer dbRegServer = regServerService.findById(id);
        dbRegServer.setStatus(Constants.YES);
        dbRegServer.setUpdateTime(new Date());
        regServerService.update(dbRegServer);
        //this.setClientCacheVersion();
        customRestConfigService.publishRegServerConfig(id);
        return new ApiResult();
    }

    /**
     * 禁用注册网关路由下的客户端
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult stop(@RequestParam Long id) {
        Assert.notNull(id, "未获取到对象ID");
        Assert.isTrue(id>0, "ID值错误");
        RegServer dbRegServer = regServerService.findById(id);
        dbRegServer.setStatus(Constants.NO);
        dbRegServer.setUpdateTime(new Date());
        regServerService.update(dbRegServer);
        //this.setClientCacheVersion();
        customRestConfigService.publishRegServerConfig(id);
        return new ApiResult();
    }

    /**
     * 禁用当前客户端，关闭其注册的所有网关路由服务端访问状态
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/stopClientAllRoute", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult stopClientAllRoute(@RequestParam String clientId) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        regServerService.stopClientAllRoute(clientId);
        customRestConfigService.publishClientConfig(clientId);
        return new ApiResult();
    }

    /**
     * 启用当前客户端，激活其注册的所有网关路由服务端访问状态
     * @param clientId
     * @return
     */
    @RequestMapping(value = "/startClientAllRoute", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult startClientAllRoute(@RequestParam String clientId) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "未获取到对象ID");
        regServerService.startClientAllRoute(clientId);
        customRestConfigService.publishClientConfig(clientId);
        return new ApiResult();
    }

    /**
     * 关闭网关路由下所有注册客户端
     * @param routeId
     * @return
     */
    @RequestMapping(value = "/stopRouteAllClient", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult stopRouteAllClient(@RequestParam String routeId) {
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        regServerService.stopRouteAllClient(routeId);
        customRestConfigService.publishRouteConfig(routeId);
        return new ApiResult();
    }

    /**
     * 启用网关路由下所有注册客户端
     * @param routeId
     * @return
     */
    @RequestMapping(value = "/startRouteAllClient", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult startRouteAllClient(@RequestParam String routeId) {
        Assert.isTrue(StringUtils.isNotBlank(routeId), "未获取到对象ID");
        regServerService.startRouteAllClient(routeId);
        customRestConfigService.publishRouteConfig(routeId);
        return new ApiResult();
    }

    /**
     * 获取未注册到的服务列表（分页）
     * @param regServerReq
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/notRegServerPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult notRegServerPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getClientId()), "未获取到客户端ID");
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return new ApiResult(regServerService.notRegServerPageList(regServerReq, currentPage, pageSize));
    }

    /**
     * 获取未注册的客户端列表（分页）
     * @param regServerReq
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/notRegClientPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult notRegClientPageList(@RequestBody RegServerReq regServerReq) {
        Assert.notNull(regServerReq, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(regServerReq.getRouteId()), "未获取路由服务ID");
        int currentPage = getCurrentPage(regServerReq.getCurrentPage());
        int pageSize = getPageSize(regServerReq.getPageSize());
        return new ApiResult(regServerService.notRegClientPageList(regServerReq, currentPage, pageSize));
    }

    /**
     * 创建Token
     * @param tokenReq
     * @return
     */
    @RequestMapping(value = "/createToken", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult createToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
        RegServer regServer = regServerService.findById(tokenReq.getRegServerId());
        Assert.notNull(regServer, "未查询到对象");
        String sub = String.format("%s,%s,%d", regServer.getRouteId(), regServer.getClientId(), System.currentTimeMillis());
        Date tokenEffectiveTime = tokenReq.getTokenEffectiveTime();
        Assert.notNull(tokenEffectiveTime, "未获取Token有效过期时间");
        String secretKey = tokenReq.getSecretKey();
        if (StringUtils.isBlank(secretKey)){
            secretKey = regServer.getClientId();
        }
        //创建Token
        String jwtToken = JwtTokenUtils.createToken(sub, tokenEffectiveTime, secretKey);
        //每次加密成功，更新数据库已有Token
        regServer.setToken(jwtToken);
        regServer.setSecretKey(secretKey);
        regServer.setTokenEffectiveTime(tokenEffectiveTime);
        regServerService.update(regServer);
        customRestConfigService.publishRegServerConfig(regServer.getId());
        //返回最新Token
        return new ApiResult(Constants.SUCCESS, jwtToken);
    }

    /**
     *清除Token
     * @param tokenReq
     * @return
     */
    @RequestMapping(value = "/removeToken", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult removeToken(@RequestBody TokenReq tokenReq){
        Assert.notNull(tokenReq, "未获取到对象");
        Assert.notNull(tokenReq.getRegServerId(), "未获取到对象ID");
        Assert.isTrue(tokenReq.getRegServerId() > 0, "ID值错误");
        RegServer regServer = regServerService.findById(tokenReq.getRegServerId());
        Assert.notNull(regServer, "未查询到对象");
        //清除已有Token
        regServer.setTokenEffectiveTime(null);
        regServer.setToken(null);
        regServer.setSecretKey(null);
        regServerService.update(regServer);
        customRestConfigService.publishRegServerConfig(regServer.getId());
        return new ApiResult();
    }

}
