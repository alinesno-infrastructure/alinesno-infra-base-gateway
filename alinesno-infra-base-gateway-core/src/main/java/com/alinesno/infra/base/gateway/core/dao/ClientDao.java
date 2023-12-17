package com.alinesno.infra.base.gateway.core.dao;

import com.alinesno.infra.base.gateway.core.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 客户端Dao数据层操作接口
 * @author jianglong
 * @date 2020/05/15
 * @version v1.0.0
 */
public interface ClientDao extends JpaRepository<Client, String> {

}
