package com.alinesno.infra.base.gateway.proxy.gateway.tiemr;

import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.RouteReqCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description 将网关路由请求的最新时间记录到redis缓存
 * @author  JL
 * @date 2021/04/15
 * @version 1.0.0
 */
@Slf4j
@Service
public class TimerRequestTimeService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 每30秒钟执行一次缓存同步
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void syncRequestTimeCache(){
        log.info("执行定时任务：网关路由请求记录同步到redis缓存...");
        ConcurrentHashMap<String,Long> cacheMap = RouteReqCache.getCacheMap();
        if (cacheMap.size() <= 0){
            return ;
        }
        ConcurrentHashMap<String,Long> dataMap = new ConcurrentHashMap<>(cacheMap.size());
        //数据对象深复制，注意：浅复制只是复制值地址，指向的存储是相同的;
        //防止数据同步过程中，写入到cacheMap，导致数据读取出错与性能影响
        dataMap.putAll(cacheMap);
        String value ;
        for (Map.Entry<String, Long> entry : dataMap.entrySet()){
            value = (String) redisTemplate.opsForHash().get(RouteConstants.SYNC_REQUEST_TIME_KEY, entry.getKey());
            if (StringUtils.isNotBlank(value)){
                //比缓存的值要大，更新它
                if (entry.getValue() < Long.parseLong(value)){
                    continue;
                }
            }
            redisTemplate.opsForHash().put(RouteConstants.SYNC_REQUEST_TIME_KEY, entry.getKey(), String.valueOf(entry.getValue()));
        }
    }
}
