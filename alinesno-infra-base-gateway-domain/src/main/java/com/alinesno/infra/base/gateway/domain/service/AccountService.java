package com.alinesno.infra.base.gateway.domain.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.domain.dao.AccountDao;
import com.alinesno.infra.base.gateway.facade.entity.Account;
import org.springframework.stereotype.Service;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class AccountService extends BaseService<Account,String, AccountDao> {
}
