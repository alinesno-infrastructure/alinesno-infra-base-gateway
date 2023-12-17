package com.alinesno.infra.base.gateway.core.service;

import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.core.bean.MonitorReq;
import com.alinesno.infra.base.gateway.core.bean.RouteRsp;
import com.alinesno.infra.base.gateway.core.dao.MonitorDao;
import com.alinesno.infra.base.gateway.core.entity.Monitor;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.util.Constants;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 告警监控业务类
 * @author JL
 * @date 2021/04/14
 * @version v1.0.0
 */
@Service
public class MonitorService extends BaseService<Monitor, String, MonitorDao> {

    @Resource
    private RouteService routeService;
    @Resource
    private MonitorDao monitorDao;

    /**
     * 获取监控的服务列表
     * @param monitorReq
     * @return
     */
    public List<Route> list(MonitorReq monitorReq){
        Route queryRoute = new Route();
        queryRoute.setOperatorId(monitorReq.getOperatorId());
        if (monitorReq != null && StringUtils.isNotBlank(monitorReq.getStatus())) {
            //如果前端搜索状态为2告警类型，则直查询路由网关状态的为0的记录
            //网关路由服务，只有0正常，1禁用，两种状态
            //网关路由服务监控，有0正常，1禁用，2告警三种状态
            if (Constants.ALARM.equals(monitorReq.getStatus())){
                queryRoute.setStatus(Constants.YES);
            }else {
                queryRoute.setStatus(monitorReq.getStatus());
            }
        }
        List<Monitor> monitorList = this.validMonitorList(monitorReq.getOperatorId());
        //没有监控数据
        if (CollectionUtils.isEmpty(monitorList)){
            return null;
        }
        Map<String, Monitor> monitorMap = monitorList.stream().collect(Collectors.toMap(Monitor::getId, r -> r));
        List<Route> resultList = new ArrayList<>(monitorMap.size());
        List<Route> routeList = routeService.list(queryRoute);
        for (Route route : routeList){
            Monitor monitor = monitorMap.get(route.getId());
            if (monitor == null){
                continue;
            }
            //如果监控状态值不等于0和1，其它状态值则表示存在告警
            if (StringUtils.equalsAny(monitor.getStatus(), Constants.YES, Constants.NO)){
            }else {
                //如果前端搜索状态为：0正常，1禁用 的网关路由服务，则不显示告警状态的网关路由服务
                if (StringUtils.equalsAny(monitorReq.getStatus(), Constants.YES, Constants.NO)){
                    continue;
                }
                route.setStatus(monitor.getStatus());
            }
            RouteRsp routeRsp = new RouteRsp();
            BeanUtils.copyProperties(route, routeRsp);
            routeRsp.setMonitor(monitor);
            resultList.add(routeRsp);
        }
        return resultList;
    }

    /**
     * 获取监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    public List<Monitor> validMonitorList(String accountId){
        return monitorDao.validMonitorList(accountId);
    }

    /**
     * 获取0正常状态的网关路由服务监控配置，告警状态：0启用，1禁用，2告警
     * @return
     */
    public List<Monitor> validRouteMonitorList(){
        return monitorDao.validRouteMonitorList();
    }

}
