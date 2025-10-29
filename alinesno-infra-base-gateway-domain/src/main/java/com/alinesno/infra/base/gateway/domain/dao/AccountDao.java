package com.alinesno.infra.base.gateway.domain.dao;

import com.alinesno.infra.base.gateway.facade.entity.StatisticalData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface AccountDao extends JpaRepository<StatisticalData, String> {

}
