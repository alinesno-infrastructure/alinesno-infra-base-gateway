package com.alinesno.infra.base.gateway.proxy.gateway.service;

import com.alinesno.infra.base.gateway.core.base.BaseGroovyService;
import com.alinesno.infra.base.gateway.core.config.ApplicationContextProvider;
import com.alinesno.infra.base.gateway.core.util.HttpEnum;
import com.alinesno.infra.base.gateway.core.util.Md5Utils;
import com.alinesno.infra.base.gateway.core.util.NetworkIpUtils;
import com.alinesno.infra.base.gateway.facade.constant.Constants;
import com.alinesno.infra.base.gateway.facade.entity.GroovyScript;
import com.alinesno.infra.base.gateway.domain.service.GroovyScriptService;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteGroovyCache;
import com.alinesno.infra.base.gateway.proxy.gateway.component.GroovyCache;
import com.alinesno.infra.base.gateway.proxy.gateway.vo.GroovyHandleData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * @description 执行网关路由下groovyScript规则引擎动态脚本
 * @author  JL
 * @date 2022/2/21
 * @version 1.0.0
 */
@Slf4j
@Service
public class DynamicGroovyService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private GroovyScriptService groovyScriptService;
    @Resource
    private ApplicationContextProvider applicationContextProvider;

    /**
     * 调用规则引擎服务，执行动态脚本
     * @param exchange
     * @param handleData
     * @param httpEnum
     * @return
     * @throws Exception
     */
    public GroovyHandleData handle(ServerWebExchange exchange, GroovyHandleData handleData, HttpEnum httpEnum) throws Exception {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        List<Long> groovyScriptIds = RouteGroovyCache.get(route.getId());
        if (CollectionUtils.isEmpty(groovyScriptIds)){
            log.debug("未找到网关ID【{}】规则引擎动态脚本【groovy】配置！", route.getId());
            return handleData;
        }
        ServerHttpRequest request = exchange.getRequest();
        String clientIp = NetworkIpUtils.getIpAddress(request);

        Map<String,String> paramMap = handleData.getParamMap();
        String body = handleData.getBody();

        BaseGroovyService groovyService;
        GroovyCache.GroovyServiceData groovyServiceData;
        for (Long id : groovyScriptIds){
            groovyServiceData = GroovyCache.getGroovyServiceData(id);
            if (groovyServiceData == null){
                log.error("未找到网关ID【{}】规则引擎动态脚本【{}】缓存配置！", route.getId(), id);
                continue;
            }

            if (httpEnum.getValue().equals(groovyServiceData.getEvent())){
                groovyService = groovyServiceData.getBaseGroovyService();
                groovyService.setRedisTemplate(redisTemplate);
                groovyService.setClientIp(clientIp);
                groovyService.setGroovyScriptId(id);
                groovyService.setRuleName(groovyServiceData.getName());
                groovyService.setRouteId(groovyServiceData.getRouteId());
                groovyService.setExtednInfo(groovyServiceData.getExtendInfo());
                groovyService.setBody(body);
                groovyService.setParamMap(paramMap);
                groovyService.apply(exchange);

                paramMap = groovyService.paramMap;
                body = groovyService.body;
            }
        }
        return new GroovyHandleData(paramMap, body);
    }

    /**
     * 调用规则引擎服务，执行动态脚本
     * @param exchange
     * @param handleData
     * @throws Exception
     */
    public GroovyHandleData requestHandle(ServerWebExchange exchange, GroovyHandleData handleData) throws Exception {
        return this.handle(exchange, handleData, HttpEnum.HTTP_REQUEST);
    }

    /**
     * 对response响应调用规则引擎服务，执行动态脚本
     * @param exchange
     * @param handleData
     * @throws Exception
     */
    public GroovyHandleData responseHandle(ServerWebExchange exchange, GroovyHandleData handleData) throws Exception {
        return this.handle(exchange, handleData, HttpEnum.HTTP_RESPONSE);
    }

    /**
     * 刷新指定groovyScript规则引擎脚本（防止有更新后未及时加载到本地内存中）
     * @param ids
     */
    public void refresh(long ... ids){
        GroovyScript script;
        for (long id : ids){
            script = groovyScriptService.findById(id);
            //当数据库查询不到此id对应的groovyScript规则引擎动态脚本记录，则表示管理端已删除该记录
            if (script == null){
                log.info("未获取groovyScript规则引擎动态脚本【{}】！", id);
                removeCacheGroovy(null, id);
                continue;
            }
            refreshGroovyScript(script);
        }
    }

    /**
     * 刷新现有加载groovyScript脚本，并缓存实例化对象
     * @param script
     */
    public void refreshGroovyScript(GroovyScript script){
        //如果是移除groovyScript规则引擎脚本，则需要清除缓存中的记录
        if (Constants.NO.equals(script.getStatus())){
            removeCacheGroovy(script.getRouteId(), script.getId());
        } else {
            String md5 = Md5Utils.md5Str(script.getId() + script.getContent());
            //编译
            instance(script, md5);
            //排序
            groovySort(script.getRouteId());
        }
    }

    /**
     * 重新排序
     * @param routeId
     */
    public void groovySort(String routeId){
        List<Long> idList = groovyScriptService.findIdList(routeId);
        if (CollectionUtils.isEmpty(idList)){
            return ;
        }
        List<Long> cacheIdList = RouteGroovyCache.get(routeId);
        if (CollectionUtils.isEmpty(cacheIdList)) {
            return ;
        }

        //转换成字符串进行比较
        String idListStr = StringUtils.join(idList, Constants.SEPARATOR_SIGN);
        String cacheIdListStr = StringUtils.join(cacheIdList, Constants.SEPARATOR_SIGN);
        //如果数据库和缓存中的排序是一样的，则不进行重排序
        if (idListStr.equals(cacheIdListStr)){
            return ;
        }

        //重新排序
        List<Long> newIdList = new ArrayList<>();
        for (Long id : idList){
            if (cacheIdList.contains(id)){
                newIdList.add(id);
            }
        }

        //重建网关缓存的规则引擎动态脚本关系
        RouteGroovyCache.remove(routeId);
        RouteGroovyCache.put(routeId, newIdList);
    }

    /**
     * 清除网关路由与groovyScript规则引擎动态脚本关系
     * @param routeId
     * @param groovyScriptId
     */
    private void removeCacheGroovy(String routeId, long groovyScriptId){
        // 查询本地groovy缓存中是否有指定ID的实例化对象
        if (GroovyCache.containsKey(groovyScriptId)) {
            GroovyCache.GroovyServiceData groovyServiceData = GroovyCache.getGroovyServiceData(groovyScriptId);
            routeId = routeId == null ? groovyServiceData.getRouteId() : routeId;
            removeCacheGroovyScript(groovyScriptId, groovyServiceData);
        }

        //移除网关与规则引擎动态脚本关系
        if (routeId != null){
            RouteGroovyCache.removeValue(routeId, groovyScriptId);
            log.info("已删除网关路由【{}】groovyScript规则引擎动态脚本【{}】！", routeId, groovyScriptId);
        }
    }


    /**
     * 清除缓存中的groovyScript规则引擎动态脚本实例对象
     * @param groovyScriptId
     * @param groovyServiceData
     */
    private void removeCacheGroovyScript(long groovyScriptId, GroovyCache.GroovyServiceData groovyServiceData){
        // 查询本地groovy缓存中是否有指定ID的实例化对象
        BaseGroovyService groovyService = groovyServiceData.getBaseGroovyService();
        // 清除spring容器中已注册的bean实例
        if (groovyService != null) {
            AutowireCapableBeanFactory beanFactory = applicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory();
            String beanName = groovyService.getClass().getName();
            if (beanFactory.containsBean(beanName)) {
                beanFactory.destroyBean(beanFactory.getBean(groovyService.getClass()));
            }
        }
        //直接从groovy缓存中移除不存在的实例化对象
        GroovyCache.removeGroovyServiceData(groovyScriptId);
    }

    /**
     * 初始化GroovyScript规则擎动态脚本，并缓存实例化对象
     * @param script
     * @param md5
     */
    public void instance(GroovyScript script, String md5){
        boolean isNews = true;
        //量否缓存过
        GroovyCache.GroovyServiceData groovyServiceData = null;
        if (GroovyCache.containsKey(script.getId())){
            groovyServiceData = GroovyCache.getGroovyServiceData(script.getId());
            String cacheMd5 = groovyServiceData.getMd5();
            //无变化，不重新加载
            if(md5.equals(cacheMd5)) {
                return;
            }
            isNews = false;
        }
        instance(script, md5 , isNews, groovyServiceData);
    }

    /**
     * 初始化GroovyScript规则擎动态脚本，并缓存实例化对象
     * @param script
     * @param md5
     * @param isNew
     */
    public void instance(GroovyScript script, String md5, boolean isNew){
        instance(script, md5, isNew, null);
    }

    /**
     * 初始化GroovyScript规则擎动态脚本，并缓存实例化对象
     * @param script
     * @param md5
     */
    public void instance(GroovyScript script, String md5, boolean isNews, GroovyCache.GroovyServiceData groovyServiceData){
        //编译
        BaseGroovyService groovyService;
        try {
            groovyService = (BaseGroovyService) groovyScriptService.instance(script);
        }catch(Exception e){
            return;
        }

        // 修改过，则先删除缓存的对象
        if (!isNews && groovyServiceData != null){
            removeCacheGroovyScript(script.getId(), groovyServiceData);
        }

        //将实例化对象注入到spring的上下文中
        applicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(groovyService);
        //本地内存缓存网关与脚本关系，用于过滤器加载指定路径网关规则引擎脚本进行验证
        RouteGroovyCache.put(script.getRouteId(), script.getId());
        //实化列对象缓存在本地内存中以提升加载性能
        GroovyCache.putGroovyServiceData(script.getId(), script.getRouteId(), script.getName(), script.getExtendInfo(), script.getEvent(), groovyService, md5);
    }

}
