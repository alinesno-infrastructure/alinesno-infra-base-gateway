package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.RegServerEntity;
import com.alinesno.infra.base.gateway.mapper.RegServerMapper;
import com.alinesno.infra.base.gateway.service.IRegServerService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegServerServiceImpl extends IBaseServiceImpl<RegServerEntity, RegServerMapper> implements IRegServerService {
    private static final Logger log = LoggerFactory.getLogger(RegServerServiceImpl.class);
}
