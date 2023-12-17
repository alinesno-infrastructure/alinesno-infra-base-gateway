package com.alinesno.infra.base.gateway.core.service;

import com.alinesno.infra.base.gateway.core.bean.CountReq;
import com.alinesno.infra.base.gateway.core.bean.CountRsp;
import com.alinesno.infra.base.gateway.core.bean.CountTotalRsp;
import com.alinesno.infra.base.gateway.core.bean.RouteCountRsp;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.util.ApiResult;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.PageResult;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @description 统计接口请求业务类
 * @author JL
 * @date 2020/12/30
 * @version v1.0.0
 */
@Service
public class CountService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RouteService routeService;

    /**
     * 查询路由集合统计结果
     * @param route
     * @param currentPage
     * @param pageSize
     * @return
     */
    public ApiResult countRouteList(Route route, int currentPage, int pageSize){
        PageResult<Route> pageResult = routeService.pageList(route,currentPage, pageSize);
        if (pageResult.getPageSize() > 0){
            //只取当天的
            String key = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
            Map<String,String> countMap = redisTemplate.opsForHash().entries(key);
            List<Route> routeList = pageResult.getLists();
            List<RouteCountRsp> routeCountRspList = routeList.stream().map(r -> {
                RouteCountRsp rsp = new RouteCountRsp();
                BeanUtils.copyProperties(r, rsp);
                //将routeId缓存的统计值取出
                String count = countMap.get(rsp.getId());
                rsp.setCount(StringUtils.isNotBlank(count) ? Integer.parseInt(count) : 0);
                return rsp;
            }).collect(Collectors.toList());
            PageResult<RouteCountRsp> pageResult1 = new PageResult<>();
            pageResult1.setCurrentPage(currentPage);
            pageResult1.setPageSize(pageSize);
            pageResult1.setTotalNum(pageResult.getTotalNum());
            pageResult1.setLists(routeCountRspList);
            return new ApiResult(pageResult1);
        }
        return new ApiResult(pageResult);
    }

    /**
     * 统计按7天和按24小时、60分钟维度计算的请求总量
     * @return
     */
    public CountTotalRsp countRequestTotal(String routeId){
        CountTotalRsp rsp = new CountTotalRsp();
        rsp.setDayCounts(this.get7DayTotalApiCount(routeId));
        rsp.setHourCounts(this.get24HourTotalApiCount(routeId));
        rsp.setMinCounts(this.get60MinTotalApiCount(routeId));
        return rsp;
    }

    /**
     * 统计按7天和按24小时维度计算的请求总量
     * @return
     */
    public CountTotalRsp countUserRequestTotal(String userId){
        CountTotalRsp rsp = new CountTotalRsp();
        rsp.setDayCounts(this.getUser7DayTotalApiCount(userId));
        rsp.setHourCounts(this.getUser24HourTotalApiCount(userId));
        return rsp;
    }

    /**
     * 统计7天内请求量数据
     */
    public CountRsp get7DayTotalApiCount(String routeId){
        return this.getCount(routeId, Constants.DAY);
    }

    /**
     * 统计24小时内请求量数据
     */
    public CountRsp get24HourTotalApiCount(String routeId){
        return this.getCount(routeId, Constants.HOUR);
    }

    /**
     * 统计60分钟内请求量数据
     */
    public CountRsp get60MinTotalApiCount(String routeId){
        return this.getCount(routeId, Constants.MIN);
    }

    /**
     * 统计7天内请求量数据
     */
    public CountRsp getUser7DayTotalApiCount(String userId){
        return this.getUserCount(null, Constants.DAY, userId);
    }

    /**
     * 统计7天内请求量数据
     */
    public CountRsp get7DayTotalApiCount(){
        return this.getCount(Constants.DAY);
    }

    /**
     * 统计24小时内请求量数据
     */
    public CountRsp getUser24HourTotalApiCount(String userId){
        return this.getUserCount(null, Constants.HOUR, userId);
    }

    /**
     * 统计24小时内请求量数据
     */
    public CountRsp get24HourTotalApiCount(){
        return this.getCount(Constants.HOUR);
    }

    /**
     * 统计天或小时维度的请求量数据
     * @param routeId
     * @param dateType
     * @return
     */
    private CountRsp getCount(String routeId, String dateType){
        Object [] infos = getCountInfo(dateType);
        List<String> dateList = (List<String>)infos[0];
        String countTag = (String)infos[1];
        List<Integer> counts = new ArrayList<>(dateList.size());
        List<String> dates = new ArrayList<>(dateList.size());
        for (String date : dateList){
            Map<String,String> countMap = redisTemplate.opsForHash().entries(countTag + date);
            AtomicReference<Integer> count = new AtomicReference<>(0);
            if (!CollectionUtils.isEmpty(countMap)){
                for (Map.Entry<String,String> entry : countMap.entrySet()){
                    //如果有指定routeId,则只取该routeId的数据
                    if (routeId != null) {
                        if (!entry.getKey().equals(routeId)){
                            continue;
                        }
                    }
                    // 如果为负载均衡的统计结果，则跳过，单独在负载均衡界面展示统计指标
                    if (entry.getKey().startsWith(RouteConstants.BALANCED)){
                        continue;
                    }
                    count.updateAndGet(v1 -> v1 + Integer.parseInt(entry.getValue()));
                }
            }
            dates.add(getFormatDateStr(date, dateType));
            counts.add(count.get());
        }
        CountRsp countRsp = new CountRsp();
        countRsp.setDates(dates);
        countRsp.setCounts(counts);
        return countRsp;
    }

    /**
     * 统计天或小时维度的请求量数据
     * @param routeId
     * @param dateType
     * @return
     */
    private CountRsp getUserCount(String routeId, String dateType, String userId){
        Object [] infos = getCountInfo(dateType);
        List<String> dateList = (List<String>)infos[0];
        String countTag = (String)infos[1];
        List<Integer> counts = new ArrayList<>(dateList.size());
        List<String> dates = new ArrayList<>(dateList.size());

        // 获取当前用户所有route
        Route qRoute = new Route();
        qRoute.setOperatorId(userId);
        List<Route> routes = routeService.findAll(qRoute);
        List<String> routeIds = Optional.ofNullable(routes)
                .orElse(Collections.emptyList())
                .stream().map(item -> item.getId()).collect(Collectors.toList());

        for (String date : dateList){
            Map<String,String> countMap = redisTemplate.opsForHash().entries(countTag + date);
            AtomicReference<Integer> count = new AtomicReference<>(0);
            if (!CollectionUtils.isEmpty(countMap) ){
                for (Map.Entry<String,String> entry : countMap.entrySet()){
                    //如果有指定routeId,则只取该routeId的数据
                    if (routeId != null) {
                        if (!entry.getKey().equals(routeId)){
                            continue;
                        }
                        if(routeIds.contains(routeId)){
                            continue;
                        }
                    }
                    // 如果为负载均衡的统计结果，则跳过，单独在负载均衡界面展示统计指标
                    if (entry.getKey().startsWith(RouteConstants.BALANCED)){
                        continue;
                    }
                    count.updateAndGet(v1 -> v1 + Integer.parseInt(entry.getValue()));
                }
            }
            dates.add(getFormatDateStr(date, dateType));
            counts.add(count.get());
        }
        CountRsp countRsp = new CountRsp();
        countRsp.setDates(dates);
        countRsp.setCounts(counts);
        return countRsp;
    }


    /**
     * 统计天或小时维度的请求量数据
     * @param dateType
     * @return
     */
    private CountRsp getCount(String dateType){
        return this.getCount(null, dateType);
    }

    /**
     * 统计路由请求数据
     * @param countReq      请求对象
     * @param isBalanced    是否负载
     * @return
     */
    public ApiResult count(CountReq countReq, boolean isBalanced){
        Assert.notNull(countReq, "未获取到对象");
        List<String> routeIds = countReq.getRouteIds();
        Assert.isTrue(routeIds != null, "未获取到路由ID");
        String dateType = StringUtils.isNotBlank(countReq.getDateType()) ? countReq.getDateType() : Constants.MIN;
        Object [] infos = getCountInfo(dateType);
        List<String> dateList = (List<String>)infos[0];
        List<String> dates = new ArrayList<>(dateList.size());
        String countTag = (String)infos[1];
        String balancedId = countReq.getBalancedId().replaceAll("-","");
        Map<String,List<Integer>> countMap = new LinkedHashMap<>(routeIds.size());
        for (String date : dateList){
            for (String routeId : routeIds){
                String key = countTag + date;
                String field = (isBalanced ? RouteConstants.BALANCED + "-" + balancedId  + "-" : "") + routeId;
                String count = (String) redisTemplate.opsForHash().get(key, field);
                List<Integer> counts = countMap.get(routeId);
                if (counts == null){
                    counts = new ArrayList<>();
                }
                counts.add(count != null ? Integer.parseInt(count) : 0);
                countMap.put(routeId, counts);
            }
            dates.add(getFormatDateStr(date, dateType));
        }

        List<CountRsp.CountData> datas = new ArrayList<>();
        CountRsp rsp = new CountRsp();
        rsp.setDatas(datas);
        rsp.setDates(dates);
        countMap.forEach((k,v)->{
            CountRsp.CountData data = new CountRsp.CountData();
            data.setRouteId(k);
            data.setCounts(v.toArray(new Integer [v.size()]));
            datas.add(data);
        });
        return new ApiResult(rsp);
    }

    /**
     * 截取时间格式，方便前端展示
     * @param date
     * @param dateType
     * @return
     */
    private String getFormatDateStr(String date, String dateType){
        if (Constants.HOUR.equals(dateType)){
            //yyyyMMddHH=>HH
            return date.substring(8);
        } else if (Constants.MIN.equals(dateType)){
            //yyyyMMddHHmm=>mm
            return date.substring(10);
        }
        return date;
    }

    /**
     * 获取统计维度信息
     * @param dateType
     * @return
     */
    private Object[] getCountInfo(String dateType){
        List<String> dates;
        String countTag ;
        if (Constants.MIN.equals(dateType)){
            dates = this.getCountMins();
            countTag =  RouteConstants.COUNT_MIN_KEY;
        }else if (Constants.HOUR.equals(dateType)){
            dates = this.getCountHours();
            countTag =  RouteConstants.COUNT_HOUR_KEY;
        }else if (Constants.DAY.equals(dateType)){
            dates = this.getCountDays();
            countTag =  RouteConstants.COUNT_DAY_KEY;
        }else {
            throw new IllegalArgumentException("统计时间类型错误");
        }
        return new Object[]{dates, countTag};
    }

    /**
     * 按天生成统计范围（计算一周内的统计值）
     * @return
     */
    private List<String> getCountDays(){
        List<String>  days = new ArrayList<>();
        for (int i = 6; i>=0; i--){
            Date dayDate = DateUtils.addDays(new Date(), - i);
            String day = DateFormatUtils.format(dayDate, Constants.YYYYMMDD);
            days.add(day);
        }
        return days;
    }

    /**
     * 按小时生成统计范围（计算一天内的统计值）
     * @return
     */
    private List<String> getCountHours(){
        List<String> hours = new ArrayList<>();
        for (int i = 23; i>=0; i--){
            hours.add(DateFormatUtils.format(DateUtils.addHours(new Date(), - i), Constants.YYYYMMDDHH));
        }
        return hours;
    }

    /**
     * 按分钟生成统计范围
     * @return
     */
    private List<String> getCountMins(){
        List<String> mins = new ArrayList<>();
        for (int i = 59; i>=0; i--){
            mins.add(DateFormatUtils.format(DateUtils.addMinutes(new Date(), - i), Constants.YYYYMMDDHHMM));
        }
        return mins;
    }



}
