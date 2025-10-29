package com.alinesno.infra.base.gateway.domain.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.domain.dao.ClientDao;
import com.alinesno.infra.base.gateway.core.util.PageResult;
import com.alinesno.infra.base.gateway.facade.entity.Client;
import com.alinesno.infra.base.gateway.facade.entity.RegServer;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description 客户端管理业务类
 * @author jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
@Service
public class ClientService extends BaseService<Client,String, ClientDao> {

    @Resource
    private RegServerService regServerService;

    /**
     * 删除客户端
     * @param client
     */
    @Override
    public void delete(Client client){
        RegServer regServer = new RegServer();
        regServer.setClientId(client.getId());
        //查找是否有注册到其它网关服务上，如有一并删除
        List<RegServer> regServerList = regServerService.findAll(regServer);
        if (!CollectionUtils.isEmpty(regServerList)){
            regServerService.delete(regServer);
        }
        super.delete(client);
    }

    /**
     * 分页查询（支持模糊查询）
     * @param client
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<Client> pageList(Client client, int currentPage, int pageSize){
        //构造条件查询方式
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(client.getName())) {
            //支持模糊条件查询
            matcher = matcher.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        return this.pageList(client, matcher, currentPage, pageSize);
    }
}
