package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.BalancedEntity;
import com.alinesno.infra.base.gateway.mapper.BalancedMapper;
import com.alinesno.infra.base.gateway.service.IBalancedService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @version 1.0.0
 */
@Service
public class BalancedServiceImpl extends IBaseServiceImpl<BalancedEntity, BalancedMapper> implements IBalancedService {
    // 日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(BalancedServiceImpl.class);
}
