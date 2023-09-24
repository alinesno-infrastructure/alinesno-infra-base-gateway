package com.alinesno.infra.base.gateway.formwork.dao;

import java.util.List;

import com.alinesno.infra.base.gateway.formwork.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @description 告警监控数据层操作接口
 * @author JL
 * @date 2021/04/14
 * @version v1.0.0
 */
public interface MonitorDao extends JpaRepository<Monitor, String> {
    /**
     * 获取监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    @Query(value ="SELECT m FROM Monitor m WHERE m.status IN ('0','2') and m.operatorId=?1 ")
    List<Monitor> validMonitorList(String operatorId);

    /**
     * 获取0正常状态的网关路由服务监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    @Query(value ="SELECT m FROM Monitor m WHERE m.status IN ('0','2') AND m.id IN (SELECT r.id FROM Route r WHERE r.status='0')")
    List<Monitor> validRouteMonitorList();
}