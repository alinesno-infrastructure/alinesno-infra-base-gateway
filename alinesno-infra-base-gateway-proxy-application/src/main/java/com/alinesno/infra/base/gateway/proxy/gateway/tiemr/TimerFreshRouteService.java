package com.alinesno.infra.base.gateway.proxy.gateway.tiemr;


import com.alinesno.infra.base.gateway.formwork.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.event.ApplicationEventPublisherFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @description 定时刷新route路由配置，如：变更配置后，会在redis中记录版本号，本地版本号不相同，则重新加载最新路由配置（已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
 * @author  JL
 * @date 2020/07/10
 * @version 1.0.0
 */
@Slf4j
//@Service
@Deprecated
public class TimerFreshRouteService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ApplicationEventPublisherFactory publisherApplicationEventFactory;

    private static String localRouteVersion = "";
    private static String localClientIdVersion = "";
    private static String localIpVersion = "";

    /**
     * 每30秒钟执行一次缓存同步
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void syncRouteCache(){
        log.info("执行定时任务：同步刷新到路由、客户端、IP等配置...");
        this.freshRoute();
        this.freshClinetId();
        this.freshIp();
    }

    /**
     * 刷新路由（重新加载路由配置到gateway缓存服务中）
     */
    private void freshRoute(){
        String routeVersion = (String) redisTemplate.opsForHash().get(RouteConstants.SYNC_VERSION_KEY, RouteConstants.ROUTE);
        if (StringUtils.isNotBlank(routeVersion)){
            if (!localRouteVersion.equals(routeVersion)){
                publisherApplicationEventFactory.publisherEvent(RouteConstants.ROUTE);
                localRouteVersion = routeVersion;
            }
        }
    }

    /**
     * 刷新ClientId（clientId用于gateway过滤器做权限拦截使用）
     */
    private void freshClinetId(){
        String clientIdVersion = (String) redisTemplate.opsForHash().get(RouteConstants.SYNC_VERSION_KEY, RouteConstants.CLIENT_ID);
        if (StringUtils.isNotBlank(clientIdVersion)){
            if (!localClientIdVersion.equals(clientIdVersion)){
                publisherApplicationEventFactory.publisherEvent(RouteConstants.CLIENT_ID);
                localClientIdVersion = clientIdVersion;
            }
        }
    }

    /**
     * 刷新IP（Ip用于gateway过滤器做权限拦截使用）
     */
    private void freshIp(){
        String ipVersion = (String) redisTemplate.opsForHash().get(RouteConstants.SYNC_VERSION_KEY, RouteConstants.IP);
        if (StringUtils.isNotBlank(ipVersion)){
            if (!localIpVersion.equals(ipVersion)){
                publisherApplicationEventFactory.publisherEvent(RouteConstants.IP);
                localIpVersion = ipVersion;
            }
        }
    }

}
