package com.alinesno.infra.base.gateway.core.service;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.gateway.core.base.BaseService;
import com.alinesno.infra.base.gateway.core.constant.StatisticalDataConstant;
import com.alinesno.infra.base.gateway.core.dao.StatisticalDataDao;
import com.alinesno.infra.base.gateway.core.entity.StatisticalData;
import com.alinesno.infra.base.gateway.core.util.Constants;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class StatisticalDataService extends BaseService<StatisticalData, String, StatisticalDataDao> {
    public JSONObject getStatisticalData(String accountId){

        JSONObject result = new JSONObject();
        StatisticalData totalStatisticalData = null;
        StatisticalData todayStatisticalData = null;

        StatisticalData qStatisticalData = new StatisticalData();
        qStatisticalData.setAccountId(accountId);
        qStatisticalData.setCountTimeKey(StatisticalDataConstant.STATISTICAL_TOTAL_KEY);
        List<StatisticalData> totalStatisticalDataList = findAll(qStatisticalData);
        if(CollectionUtils.isEmpty(totalStatisticalDataList)){
            totalStatisticalData = new StatisticalData();
            todayStatisticalData = new StatisticalData();

            totalStatisticalData.setTotalCount(0L);
            totalStatisticalData.setSuccessCount(0L);
            totalStatisticalData.setFailCount(0L);

            todayStatisticalData.setTotalCount(0L);
            todayStatisticalData.setSuccessCount(0L);
            todayStatisticalData.setFailCount(0L);

            result.put("historyCount", totalStatisticalData);
            result.put("todayCount", todayStatisticalData);

            return result;
        }

        totalStatisticalData = totalStatisticalDataList.get(0);

        String dayKey = StatisticalDataConstant.STATISTICAL_DAY_KEY + DateFormatUtils.format(new Date(), Constants.YYYYMMDD);
        qStatisticalData.setCountTimeKey(dayKey);
        List<StatisticalData> todayStatisticalDataList = findAll(qStatisticalData);
        if(CollectionUtils.isEmpty(todayStatisticalDataList)){
            todayStatisticalData = new StatisticalData();
            todayStatisticalData.setTotalCount(0L);
            todayStatisticalData.setSuccessCount(0L);
            todayStatisticalData.setFailCount(0L);
        } else {
            todayStatisticalData = todayStatisticalDataList.get(0);
        }

        result.put("historyCount", totalStatisticalData);
        result.put("todayCount", todayStatisticalData);

        return result;
    }

    public JSONObject getCoordinateData(String accountId){

        JSONObject result = new JSONObject();

        long[] successCounts = new long[30];
        long[] failCounts = new long[30];

        // 查询统计表
        StatisticalData qStatisticalData = new StatisticalData();
        qStatisticalData.setAccountId(accountId);
        List<StatisticalData> statisticalDataList = findAll(qStatisticalData);

        if(CollectionUtils.isEmpty(statisticalDataList)){
            result.put("successCounts",successCounts);
            result.put("failCounts",failCounts);
            return result;
        }

        Map<String, Long> timeSuccessData = generateTimeData();
        Map<String, Long> timeFailData = generateTimeData();

        for(StatisticalData statisticalData : statisticalDataList){
                String key = statisticalData.getCountTimeKey();
                if(timeSuccessData.containsKey(key)){
                    timeSuccessData.put(key, Optional.ofNullable(statisticalData.getSuccessCount()).orElse(0L));
                    timeFailData.put(key, Optional.ofNullable(statisticalData.getFailCount()).orElse(0L));
                }
        }
        Long[] longValues = timeSuccessData.values().toArray(new Long[30]);
        successCounts = Arrays.stream(longValues).mapToLong(Long::longValue).toArray();

        longValues = timeFailData.values().toArray(new Long[30]);
        failCounts = Arrays.stream(longValues).mapToLong(Long::longValue).toArray();

        result.put("successCounts",successCounts);
        result.put("failCounts",failCounts);
        return result;
    }

    private Map<String, Long> generateTimeData(){

        Map<String, Long> result = new LinkedHashMap<>();

        for(int i = -29; i <= 0; ++i){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            Date searchData = calendar.getTime();
            String key = StatisticalDataConstant.STATISTICAL_DAY_KEY + DateFormatUtils.format(searchData, Constants.YYYYMMDD);
            result.put(key, 0L);
        }

        return result;
    }
}
