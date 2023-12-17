package com.alinesno.infra.base.gateway.core.dao;

import com.alinesno.infra.base.gateway.core.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoxiaodong
 * @version 1.0.0 17:14
 */
public interface StatisticalDataDao extends JpaRepository<Account, String> {
}
