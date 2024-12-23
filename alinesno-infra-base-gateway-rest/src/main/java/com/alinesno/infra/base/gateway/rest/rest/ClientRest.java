package com.alinesno.infra.base.gateway.rest.rest;

import com.alinesno.infra.base.gateway.core.base.BaseRest;
import com.alinesno.infra.base.gateway.core.bean.ClientReq;
import com.alinesno.infra.base.gateway.core.entity.Client;
import com.alinesno.infra.base.gateway.core.service.ClientService;
import com.alinesno.infra.base.gateway.core.service.CustomRestConfigService;
import com.alinesno.infra.base.gateway.core.util.ApiResult;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.UUIDUtils;
import com.alinesno.infra.base.gateway.rest.aop.DataFilter;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @description 客户端管理控制器类
 * @author  jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
@RestController
@RequestMapping("/client")
public class ClientRest extends BaseRest<Client> {

    @Resource
    private ClientService clientService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomRestConfigService customRestConfigService;

    /**
     * 添加客户端
     * @param client
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public ApiResult add(@RequestBody Client client) {
        Assert.notNull(client, "未获取到对象");

        client.setId(UUIDUtils.getUUIDString());
        client.setCreateTime(new Date());

        this.validate(client);

        //验证名称是否重复
        Client qClient = new Client();
        qClient.setName(client.getName());
        qClient.setOperatorId(client.getOperatorId());
        long count = clientService.count(qClient);

        Assert.isTrue(count <= 0, "客户端名称已存在，不能重复");

        //保存
        clientService.save(client);
        customRestConfigService.publishClientConfig(client.getId());
        return new ApiResult();
    }

    /**
     * 删除客户端
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult delete(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Client dbClient = clientService.findById(id);
        Assert.notNull(dbClient, "未获取到对象");
        clientService.delete(dbClient);
        customRestConfigService.publishClientConfig(id);
        return new ApiResult();
    }

    /**
     * 更新客户端
     * @param client
     * @return
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public ApiResult update(@RequestBody Client client) {
        Assert.notNull(client, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(client.getId()), "未获取到对象ID");
        client.setUpdateTime(new Date());
        this.validate(client);
        clientService.update(client);
        customRestConfigService.publishClientConfig(client.getId());
        return new ApiResult();
    }

    /**
     * 查询客户端
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult findById(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        return new ApiResult(clientService.findById(id));
    }

    /**
     * 分页查询客户端
     * @param clientReq
     * @return
     */
    @DataFilter
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult pageList(@RequestBody ClientReq clientReq) {
        Client client = new Client();
        Integer reqCurrentPage = null;
        Integer reqPageSize = null;
        if (clientReq != null) {
            reqCurrentPage = clientReq.getCurrentPage();
            reqPageSize = clientReq.getPageSize();
            BeanUtils.copyProperties(clientReq, client);
            if (StringUtils.isBlank(client.getName())) {
                client.setName(null);
            }
            if (StringUtils.isBlank(client.getIp())) {
                client.setIp(null);
            }
            if (StringUtils.isBlank(client.getGroupCode())) {
                client.setGroupCode(null);
            }
            if (StringUtils.isBlank(client.getStatus())) {
                client.setStatus(null);
            }
        }
        int currentPage = getCurrentPage(reqCurrentPage);
        int pageSize = getPageSize(reqPageSize);
        return new ApiResult(clientService.pageList(client, currentPage, pageSize));
    }

    /**
     * 设置客户端状态为启用
     * @param id
     * @return
     */
    @RequestMapping(value = "/start", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult start(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Client dbClient = clientService.findById(id);
        dbClient.setStatus(Constants.YES);
        clientService.update(dbClient);
        customRestConfigService.publishClientConfig(id);
        return new ApiResult();
    }

    /**
     * 设置客户端状态为禁用
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult stop(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到对象ID");
        Client dbClient = clientService.findById(id);
        dbClient.setStatus(Constants.NO);
        clientService.update(dbClient);
        customRestConfigService.publishClientConfig(id);
        return new ApiResult();
    }

}
