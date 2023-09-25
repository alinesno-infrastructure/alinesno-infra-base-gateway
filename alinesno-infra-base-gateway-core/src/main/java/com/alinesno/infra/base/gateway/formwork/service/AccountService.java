package com.alinesno.infra.base.gateway.formwork.service;

import com.alinesno.infra.base.gateway.formwork.base.BaseService;
import com.alinesno.infra.base.gateway.formwork.dao.AccountDao;
import com.alinesno.infra.base.gateway.formwork.entity.Account;
import org.springframework.stereotype.Service;

/**
 * @author luoxiaodong
 * @data 2023/04/25 17:28
 */
@Service
public class AccountService extends BaseService<Account,String, AccountDao> {
}
