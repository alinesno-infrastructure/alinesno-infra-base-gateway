package com.alinesno.infra.base.gateway.formwork.dao;

import com.alinesno.infra.base.gateway.formwork.entity.Balanced;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 负载管理数据层操作接口
 * @author jianglong
 * @date 2020/06/28
 * @version v1.0.0
 */
public interface BalancedDao extends JpaRepository<Balanced, Long> {

}
