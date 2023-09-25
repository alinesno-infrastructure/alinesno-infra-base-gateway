package com.alinesno.infra.base.gateway.formwork.dao;

import java.util.List;
import java.util.Map;

import com.alinesno.infra.base.gateway.formwork.entity.LoadServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @description 负载服务数据层操作接口
 * @author jianglong
 * @date 2020/06/28
 * @version v1.0.0
 */
public interface LoadServerDao extends JpaRepository<LoadServer, Long> {

    /**
     * 删除负载下所有的路由服务
     * @param balancedId
     */
    void deleteAllByBalancedId(String balancedId);

    /**
     * 查询指定负载下所有路由服务
     * @param balancedId
     * @return
     */
    List<LoadServer> queryByBalancedId(String balancedId);

    /**
     * 查询指定路由关联的负载服务
     * @param routeId
     * @return
     */
    List<LoadServer> queryByRouteId(String routeId);

    @Query(value = "SELECT r.routeId as settingId, r.name,r.groupCode,r.uri,r.path,r.method,r.status,l.id,l.routeId,l.weight FROM route r INNER JOIN loadserver l ON r.id=l.routeId WHERE l.balancedId=?1",
            nativeQuery = true)
    List<Map> queryLoadServerList(String balancedId);


}
