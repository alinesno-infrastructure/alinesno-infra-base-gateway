package com.alinesno.infra.base.gateway.formwork.dao;

import com.alinesno.infra.base.gateway.formwork.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoxiaodong
 * @version 1.0.0 17:14
 */
public interface StatisticalDataDao extends JpaRepository<Account, String> {
}
