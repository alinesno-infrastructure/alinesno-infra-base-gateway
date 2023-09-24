package com.alinesno.infra.base.gateway.formwork.dao;

import com.alinesno.infra.base.gateway.formwork.entity.SecureIp;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description IP管理Dao数据层操作接口
 * @author jianglong
 * @date 2020/05/28
 * @version v1.0.0
 */
public interface SecureIpDao extends JpaRepository<SecureIp, String> {
}
