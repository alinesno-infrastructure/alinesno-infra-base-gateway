package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.MonitorEntity;
import com.alinesno.infra.base.gateway.mapper.MonitorMapper;
import com.alinesno.infra.base.gateway.service.IMonitorService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonitorServiceImpl extends IBaseServiceImpl<MonitorEntity, MonitorMapper> implements IMonitorService {
    private static final Logger log = LoggerFactory.getLogger(MonitorServiceImpl.class);
}
