package com.alinesno.infra.base.gateway.core.dao;

import com.alinesno.infra.base.gateway.core.entity.StatisticalData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface AccountDao extends JpaRepository<StatisticalData, String> {

}
