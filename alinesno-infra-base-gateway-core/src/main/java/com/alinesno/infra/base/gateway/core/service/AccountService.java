package com.alinesno.infra.base.gateway.core.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.core.dao.AccountDao;
import com.alinesno.infra.base.gateway.core.entity.Account;
import org.springframework.stereotype.Service;

/**
 * @author luoxiaodong
 * @data 2023/04/25 17:28
 */
@Service
public class AccountService extends BaseService<Account,String, AccountDao> {
}
