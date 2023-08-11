package com.alinesno.infra.base.gateway.service.impl;

import com.alinesno.infra.base.gateway.entity.AccountEntity;
import com.alinesno.infra.base.gateway.mapper.AccountMapper;
import com.alinesno.infra.base.gateway.service.IAccountService;
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
public class AccountServiceImpl extends IBaseServiceImpl<AccountEntity, AccountMapper> implements IAccountService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
}
