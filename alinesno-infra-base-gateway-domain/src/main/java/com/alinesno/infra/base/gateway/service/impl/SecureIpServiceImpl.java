package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.SecureIpEntity;
import com.alinesno.infra.base.gateway.mapper.SecureIpMapper;
import com.alinesno.infra.base.gateway.service.ISecureIpService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecureIpServiceImpl extends IBaseServiceImpl<SecureIpEntity, SecureIpMapper> implements ISecureIpService {
    private static final Logger log = LoggerFactory.getLogger(SecureIpServiceImpl.class);
}
