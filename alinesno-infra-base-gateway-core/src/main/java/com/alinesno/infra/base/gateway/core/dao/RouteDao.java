package com.alinesno.infra.base.gateway.core.dao;

import com.alinesno.infra.base.gateway.core.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description 网关服务Dao数据层操作接口
 * @author jianglong
 * @date 2020/05/14
 * @version v1.0.0
 */
public interface RouteDao extends JpaRepository<Route, String> {
    /**
     * 查询开启监控的网关路由服务,条件：网关状态为0正常，监控状态为：0正常或(2告警+0可重试)
     * @return
     */
    @Query(value ="SELECT r FROM Route r WHERE r.status='0' AND r.id in (select m.id from Monitor m where m.status='0' or (m.status='2' and m.recover='0'))")
    List<Route> monitorRouteList();
}
