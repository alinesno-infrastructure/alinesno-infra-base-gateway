package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.ClientEntity;
import com.alinesno.infra.base.gateway.mapper.ClientMapper;
import com.alinesno.infra.base.gateway.service.IClientService;
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
public class ClientServiceImpl extends IBaseServiceImpl<ClientEntity, ClientMapper> implements IClientService {
    // 日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);
}
