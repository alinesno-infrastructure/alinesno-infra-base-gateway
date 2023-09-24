package com.alinesno.infra.base.gateway.formwork.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alinesno.infra.base.gateway.formwork.base.BaseService;
import com.alinesno.infra.base.gateway.formwork.dao.RegServerDao;
import com.alinesno.infra.base.gateway.formwork.entity.RegServer;
import com.alinesno.infra.base.gateway.formwork.util.Constants;
import com.alinesno.infra.base.gateway.formwork.util.PageResult;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 注册服务业务管理类
 * @author jianglong
 * @date 2020/05/16
 * @version v1.0.0
 */
@Service
public class RegServerService extends BaseService<RegServer,Long, RegServerDao> {

    @Autowired
    private RegServerDao regServerDao;

    private static final String IS_TIMEOUT = "isTimeout";
    private static final String TOKEN_EFFECTIVE_TIME = "tokenEffectiveTime";

    /**
     * 停止客户端下所有路由服务的访问（状态置为1，禁止通行）
     * @param clientId
     */
    public void stopClientAllRoute(String clientId){
        regServerDao.setClientAllRouteStatus(clientId, Constants.YES,Constants.NO);
    }

    /**
     * 启动客户端下所有路由服务的访问（状态置为0，允许通行）
     * @param clientId
     */
    public void startClientAllRoute(String clientId){
        regServerDao.setClientAllRouteStatus(clientId,Constants.NO,Constants.YES);
    }

    /**
     * 停止路由服务下所有客户端的访问（状态置为1，禁止通行）
     * @param routeId
     */
    public void stopRouteAllClient(String routeId){
        regServerDao.setRouteAllClientStatus(routeId,Constants.YES,Constants.NO);
    }

    /**
     * 启动路由服务下所有客户端的访问（状态置为0，允许通行）
     * @param routeId
     */
    public void startRouteAllClient(String routeId){
        regServerDao.setRouteAllClientStatus(routeId,Constants.NO,Constants.YES);
    }

    /**
     * 查询当所有已注册的客户端
     * @return
     */
    public List allRegClientList(){
        return regServerDao.allRegClientList();
    }

    /**
     * 查询指定客户端注册的所有网关路由服务
     * @param clientId
     * @return
     */
    public List getRegClientList(String clientId){
        return regServerDao.getRegClientList(clientId);
    }

    /**
     * 查询指定网关服务下的注册的所有客户端
     * @param routeId
     * @return
     */
    public List getByRouteRegClientList(String routeId){
        return regServerDao.getByRouteRegClientList(routeId);
    }

    /**
     * 查询当前网关路由服务下已注册的客户端
     * @param regServer
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String,Object>> regClientList(RegServer regServer){
        String sql = "SELECT s.id AS regServerId,s.status AS regServerStatus,DATE_FORMAT(s.createTime,'%Y-%m-%d %H:%i:%s') as regServerTime,c.* FROM client c, regserver s WHERE c.id = s.clientId AND s.routeId=?";
        return nativeQuery(sql, Arrays.asList(regServer.getRouteId()));
    }

    /**
     * 查询当前网关路由服务下已注册的客户端
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult clientPageList(RegServer regServer, int currentPage, int pageSize){
        //DATE_FORMAT(s.tokenEffectiveTime,'%Y-%m-%d %H:%i:%s') as tokenEffectiveTime
        String sql = "SELECT s.id AS regServerId,s.status AS regServerStatus,DATE_FORMAT(s.createTime,'%Y-%m-%d %H:%i:%s') as regServerTime,s.token,s.secretKey,s.tokenEffectiveTime,c.* FROM client c, regserver s WHERE c.id = s.clientId AND s.routeId=?";
        PageResult pageResult = pageNativeQuery(sql, Arrays.asList(regServer.getRouteId()), currentPage, pageSize);
        List<Map<String, Object>> list = pageResult.getLists();
        if (list != null){
            long nowTime = System.currentTimeMillis();
            for (Map<String, Object> map : list){
                Object tokenEffectiveTime = map.get(TOKEN_EFFECTIVE_TIME);
                if (tokenEffectiveTime instanceof java.sql.Timestamp){
                    Timestamp timestamp = (Timestamp) tokenEffectiveTime;
                    map.put(TOKEN_EFFECTIVE_TIME, DateFormatUtils.format(new Date(timestamp.getTime()),Constants.YYYY_MM_DD_HH_MM_SS));
                    if (timestamp.getTime() < nowTime){
                        map.put(IS_TIMEOUT, Constants.NO);
                        continue;
                    }
                }
                map.put(IS_TIMEOUT, Constants.YES);
            }
        }
        return pageResult;
    }

    /**
     * 查询当前客户端已注册的网关路由服务
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult serverPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT s.id AS regServerId,s.status as regServerStatus,DATE_FORMAT(s.createTime,'%Y-%m-%d %H:%i:%s') as regServerTime,r.* FROM route r, regserver s WHERE r.id = s.routeId and s.clientId=?";
        return pageNativeQuery(sql, Arrays.asList(regServer.getClientId()), currentPage, pageSize);
    }

    /**
     * 查询当前网关路由服务下没有注册的客户端
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notRegClientPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT c.id,c.name,c.groupCode,c.ip FROM client c WHERE c.status='0' AND operatorId=? AND id NOT IN (SELECT s.clientId FROM regserver s WHERE s.routeId=?)";
        return pageNativeQuery(sql, Arrays.asList(regServer.getOperatorId(), regServer.getRouteId()), currentPage, pageSize);
    }

    /**
     * 查询当前客户端没有注册的网关路由服务
     * @param regServer
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult notRegServerPageList(RegServer regServer, int currentPage, int pageSize){
        String sql = "SELECT r.id,r.routeId,r.name,r.uri,r.path,r.status FROM route r WHERE r.status='0' AND operatorId=? AND r.id NOT IN (SELECT s.routeId FROM regserver s WHERE s.clientId=?)";
        return pageNativeQuery(sql, Arrays.asList(regServer.getOperatorId(), regServer.getClientId()), currentPage, pageSize);
    }


}
