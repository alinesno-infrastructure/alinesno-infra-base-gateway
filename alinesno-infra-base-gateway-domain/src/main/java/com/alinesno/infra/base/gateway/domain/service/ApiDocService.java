package com.alinesno.infra.base.gateway.domain.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.domain.dao.ApiDocDao;
import com.alinesno.infra.base.gateway.facade.entity.ApiDoc;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

/**
 * @description 网关路由API接口文档业务实现类
 * @author JL
 * @date 2020/11/25
 * @version v1.0.0
 */
@Service
public class ApiDocService extends BaseService<ApiDoc, String, ApiDocDao> {
    private EntityManager entityManager;
}
