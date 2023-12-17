package com.alinesno.infra.base.gateway.core.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.core.dao.BalancedDao;
import com.alinesno.infra.base.gateway.core.entity.Balanced;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 负载管理业务实现类
 * @author jianglong
 * @date 2020/06/28
 * @version v1.0.0
 */
@Service
public class BalancedService extends BaseService<Balanced, String, BalancedDao> {

    @Resource
    private LoadServerService loadServerService;

    /**
     * 删除负载以及注册到负载的路由服务
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
    public void deleteAndServer(String id){
        loadServerService.deleteAllByBalancedId(id);
        this.deleteById(id);
    }
}
