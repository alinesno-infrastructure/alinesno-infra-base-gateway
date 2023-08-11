package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.RouteEntity;
import com.alinesno.infra.base.gateway.mapper.RouteMapper;
import com.alinesno.infra.base.gateway.service.IRouteService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl extends IBaseServiceImpl<RouteEntity, RouteMapper> implements IRouteService {
    private static final Logger log = LoggerFactory.getLogger(RouteServiceImpl.class);
}
