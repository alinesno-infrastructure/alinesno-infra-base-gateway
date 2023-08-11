package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.ApiDocEntity;
import com.alinesno.infra.base.gateway.mapper.ApiDocMapper;
import com.alinesno.infra.base.gateway.service.IApiDocService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 数据资产统计Service业务层处理
 *
 * @version 1.0.0
 */
@Service
public class ApiDocServiceImpl extends IBaseServiceImpl<ApiDocEntity, ApiDocMapper> implements IApiDocService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ApiDocServiceImpl.class);
}
