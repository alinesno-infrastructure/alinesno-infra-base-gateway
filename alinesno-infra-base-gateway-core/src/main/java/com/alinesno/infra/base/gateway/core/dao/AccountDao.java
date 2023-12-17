package com.alinesno.infra.base.gateway.core.dao;

import com.alinesno.infra.base.gateway.core.entity.StatisticalData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luoxiaodong
 * @data 2023/04/25 17:26
 */
public interface AccountDao extends JpaRepository<StatisticalData, String> {

}
