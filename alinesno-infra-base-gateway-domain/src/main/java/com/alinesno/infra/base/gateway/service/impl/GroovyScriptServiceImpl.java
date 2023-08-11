package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.GroovyScriptEntity;
import com.alinesno.infra.base.gateway.mapper.GroovyScriptMapper;
import com.alinesno.infra.base.gateway.service.IGroovyScriptService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GroovyScriptServiceImpl extends IBaseServiceImpl<GroovyScriptEntity, GroovyScriptMapper> implements IGroovyScriptService {
    private static final Logger log = LoggerFactory.getLogger(GroovyScriptServiceImpl.class);
}
