package com.alinesno.infra.base.gateway.proxy.gateway.tiemr;

import cn.hutool.core.collection.CollectionUtil;
import com.alinesno.infra.base.gateway.core.constant.StatisticalDataConstant;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.entity.StatisticalData;
import com.alinesno.infra.base.gateway.core.service.RouteService;
import com.alinesno.infra.base.gateway.core.service.StatisticalDataService;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import com.alinesno.infra.base.gateway.core.util.UUIDUtils;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.CountCache;
import com.alinesno.infra.base.gateway.proxy.gateway.cache.ErrorCountCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description 定时执行路由访问量统计业务类
 * @author  jianglong
 * @date 2020/07/07
 * @version 1.0.0
 */
@Slf4j
@Service
public class TimerCountService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StatisticalDataService statisticalDataService;

    @Resource
    private RouteService routeService;

    private static final String FRESH = ":FRESH";

    /**
     * 每1分钟执行一次缓存同步
     */
    @Scheduled(cron = "0 * * * * ?")
    public void writerCache(){
        log.info("执行定时任务：统计数据同步到redis缓存...");
        //保存按分钟统计的数据
        ConcurrentHashMap<String,Integer> cacheMap = CountCache.getCacheMap();
        ConcurrentHashMap<String,Integer> errorCacheMap = ErrorCountCache.getCacheMap();
        //深克隆map
        ConcurrentHashMap<String,Integer> minMap = new ConcurrentHashMap<>(cacheMap.size());
        ConcurrentHashMap<String,Integer> minErrorMap = new ConcurrentHashMap<>(errorCacheMap.size());
        minMap.putAll(cacheMap);
        minErrorMap.putAll(errorCacheMap);
        //记录到统计表
        renewStatistical(minMap, minErrorMap);

        log.debug("cacheMap = {}" , cacheMap);

        //每次统计完清除缓存统计数据，重新记录下一分钟请求量
        CountCache.clear();

        //保存按分钟统计的数据,数据缓存1小时
        String freshKey = RouteConstants.COUNT_MIN_KEY + FRESH;
        String minKey = RouteConstants.COUNT_MIN_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDDHHMM);
        String min =  DateFormatUtils.format(new Date(), Constants.YYYYMMDDHHMM);
        String minFresh = (String) redisTemplate.opsForValue().get(freshKey);
        if (minFresh == null || Long.parseLong(min) > Long.parseLong(minFresh)){
            this.recordCountCache(freshKey, min, minKey, minMap, 1, TimeUnit.HOURS);
        }else {
            this.syncCountCache(minKey, minMap);
        }

        //保存按小时统计的数据,数据缓存24小时
        freshKey = RouteConstants.COUNT_HOUR_KEY + FRESH;
        String hourKey = RouteConstants.COUNT_HOUR_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDDHH);
        String hour =  DateFormatUtils.format(new Date(), Constants.YYYYMMDDHH);
        String hourFresh = (String) redisTemplate.opsForValue().get(freshKey);
        if (hourFresh == null || Long.parseLong(hour) > Long.parseLong(hourFresh)){
            this.recordCountCache(freshKey, hour, hourKey, minMap, 24, TimeUnit.HOURS);
        }else {
            this.syncCountCache(hourKey, minMap);
        }

        //保存按天统计的数据,数据缓存7天
        freshKey = RouteConstants.COUNT_DAY_KEY + FRESH;
        String dayKey = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDD);
        String day =  DateFormatUtils.format(new Date(), Constants.YYYYMMDD);
        String dayFresh = (String) redisTemplate.opsForValue().get(freshKey);
        if (dayFresh == null || Long.parseLong(day) > Long.parseLong(dayFresh)){
            this.recordCountCache(freshKey, day, dayKey, minMap, 7, TimeUnit.DAYS);
        }else {
            this.syncCountCache(dayKey, minMap);
        }
    }

    /**
     * 设置缓存数据
     * @param freshKey
     * @param freshValue
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     */
    private void recordCountCache(String freshKey, String freshValue, String key, ConcurrentHashMap<String,Integer> value, int timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(freshKey, freshValue);
        boolean exist = redisTemplate.hasKey(key);
        Map<String,String> cacheMap = new HashMap<>(value.size());
        value.forEach((k,v)->cacheMap.put(k,String.valueOf(v)));
        redisTemplate.opsForHash().putAll(key, cacheMap);
        //新增key设置过期时间
        if (!exist){
            //redis过期清除指定KEY缓存数据
            redisTemplate.expire(key, timeout, timeUnit);
        }
    }

    /**
     * 考虑网关服务的集群式架构设计，需要累加已知统计缓存数据
     * @param key
     * @param map
     */
    public void syncCountCache(String key, Map<String,Integer> map){
        map.forEach((k,v)->{
            String value = (String) redisTemplate.opsForHash().get(key, k);
            if (StringUtils.isNotBlank(value)){
                int count = 0;
                try{
                    count = Integer.parseInt(value);
                }catch(Exception e){
                    log.error("redis get key:{} & field:{} to value is null", key, k);
                }
                v += count;
            }

            log.debug("key = {} , k = {} , value = {}" , key , k , v);

            redisTemplate.opsForHash().put(key, k, String.valueOf(v));
        });
    }


    /**
     * 更新用户请求统计信息
     * @param totalCache
     * @param errorCache
     */
    private void renewStatistical(ConcurrentHashMap<String,Integer> totalCache,  ConcurrentHashMap<String,Integer> errorCache) {
        // 构建查询key
        log.info("更新统计表");
        String dayKey = StatisticalDataConstant.STATISTICAL_DAY_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDD);

        StatisticalData qStatisticalData = new StatisticalData();
        Iterator<String> iterator = totalCache.keys().asIterator();
        while (iterator.hasNext()){
            String routeId =  iterator.next();
            String qRouteId = null;
            if(routeId.startsWith(RouteConstants.BALANCED)){
                String[] strItems = routeId.split("-");
                qRouteId = strItems[2];
            } else {
                qRouteId = routeId;
            }
            Route route = routeService.findById(qRouteId);
//            String userId = route.getOperatorId();

            // 获取要更新表项
//            qStatisticalData.setAccountId(userId);
            List<StatisticalData> statisticalDataList = statisticalDataService.findAll(qStatisticalData);
            StatisticalData statisticalDataTotal = null;
            StatisticalData statisticalDataDay = null;
            if(CollectionUtil.isEmpty(statisticalDataList)){
                statisticalDataTotal = new StatisticalData();
                statisticalDataDay = new StatisticalData();
                statisticalDataTotal.setId(UUIDUtils.getUUIDString());
//                statisticalDataTotal.setAccountId(userId);
                statisticalDataTotal.setCountTimeKey(StatisticalDataConstant.STATISTICAL_TOTAL_KEY);
                statisticalDataDay.setId(UUIDUtils.getUUIDString());
//                statisticalDataDay.setAccountId(userId);
                statisticalDataDay.setCountTimeKey(dayKey);
            } else {
                statisticalDataTotal = statisticalDataList.stream()
                        .filter(item -> item.getCountTimeKey().contains(StatisticalDataConstant.STATISTICAL_TOTAL_KEY))
                        .collect(Collectors.toList()).get(0);
                List<StatisticalData> errorStatisticalDataList = statisticalDataList.stream()
                        .filter(item -> item.getCountTimeKey().contains(dayKey)).collect(Collectors.toList());
                if(CollectionUtil.isEmpty(errorStatisticalDataList)){
                    statisticalDataDay = new StatisticalData();
                    statisticalDataDay.setId(UUIDUtils.getUUIDString());
//                    statisticalDataDay.setAccountId(userId);
                    statisticalDataDay.setCountTimeKey(dayKey);
                } else {
                    statisticalDataDay = errorStatisticalDataList.get(0);
                }
            }

            long totalCount = Optional.ofNullable(totalCache.get(routeId)).orElse(0);
            long failCount ;
            if(errorCache.containsKey(routeId)){
                failCount = Optional.of(totalCache.get(routeId)).orElse(0);
            } else {
                failCount = 0L;
            }
            long successCount = totalCount - failCount;

            // 更新表
            statisticalDataTotal.setTotalCount(Optional.ofNullable(statisticalDataTotal.getTotalCount()).orElse(0L) + totalCount);
            statisticalDataTotal.setFailCount(Optional.ofNullable(statisticalDataTotal.getFailCount()).orElse(0L) + failCount);
            statisticalDataTotal.setSuccessCount(Optional.ofNullable(statisticalDataTotal.getSuccessCount()).orElse(0L) + successCount);

            statisticalDataDay.setTotalCount(Optional.ofNullable(statisticalDataDay.getTotalCount()).orElse(0L) + totalCount);
            statisticalDataDay.setFailCount(Optional.ofNullable(statisticalDataDay.getFailCount()).orElse(0L) + failCount);
            statisticalDataDay.setSuccessCount(Optional.ofNullable(statisticalDataDay.getSuccessCount()).orElse(0L) + successCount);

            statisticalDataService.update(statisticalDataTotal);
            statisticalDataService.update(statisticalDataDay);

            // 清理一个月之前数据
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1); // 获取一个月前的日期
            Date clearData = calendar.getTime();
            String clearKey = StatisticalDataConstant.STATISTICAL_DAY_KEY + DateFormatUtils.format(clearData, Constants.YYYYMMDD);
            StatisticalData qClearStatisticalData = new StatisticalData();
//            qClearStatisticalData.setAccountId(userId);
            qClearStatisticalData.setCountTimeKey(clearKey);
            List<StatisticalData> clearStatisticalDataList = statisticalDataService.findAll(qClearStatisticalData);
            if(CollectionUtil.isNotEmpty(clearStatisticalDataList)){
                for(StatisticalData clearStatisticalData : clearStatisticalDataList){
                    statisticalDataService.delete(clearStatisticalData);
                }
            }
        }


    }

}
