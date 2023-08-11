package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.StatisticalEntity;
import com.alinesno.infra.base.gateway.mapper.StatisticalMapper;
import com.alinesno.infra.base.gateway.service.IStatisticalService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatisticalServiceImpl extends IBaseServiceImpl<StatisticalEntity, StatisticalMapper> implements IStatisticalService {
    private static final Logger log = LoggerFactory.getLogger(StatisticalServiceImpl.class);
}
