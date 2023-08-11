package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.LoadServerEntity;
import com.alinesno.infra.base.gateway.mapper.LoadServerMapper;
import com.alinesno.infra.base.gateway.service.ILoadServerService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoadServerServiceImpl extends IBaseServiceImpl<LoadServerEntity, LoadServerMapper> implements ILoadServerService {
    private static final Logger log = LoggerFactory.getLogger(LoadServerServiceImpl.class);
}
