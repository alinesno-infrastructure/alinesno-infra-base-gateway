package com.alinesno.infra.base.gateway.proxy.gateway.filter.authorize;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 验证请求是否在可访问时间段内
 * @author  jianglong
 * @date 2020/05/25
 * @version 1.0.0
 */
@Slf4j
public class TimeFilter extends FilterHandler {

    public TimeFilter(FilterHandler handler){
        this.handler = handler;
    }

    @Override
    public void handleRequest(ServerHttpRequest request){
        if (route.getFilterAuthorizeName().contains("time")){
            log.info("处理网关路由请求{},执行time过滤 ", route.getId());
            String accessTime = route.getAccessTime();
            Assert.isTrue(StringUtils.isNotBlank(accessTime),"自定义time时间段不能为空");
            Assert.isTrue(accessTime.contains(","),"自定义time时间段格式错误");
            String [] times = accessTime.split(",");
            String date = DateFormatUtils.format(new Date(), "YYYY-MM-DD");
            long startTime = this.getDateTime(date, times[0]);
            long endTime = this.getDateTime(date, times[1]);
            long nowTime = System.currentTimeMillis();
            if (nowTime >= startTime && nowTime <= endTime){
            }else {
                throw new IllegalStateException("执行time过滤,自定义time时间段访问验证不通过! 请求源="+ DateFormatUtils.format(new Date(nowTime), "HH:mm:ss")+",自定义目标"+times[0]+"," + times[1]);
            }
        }
    }

    /**
     * 获取拼装时间的long值
     * @param date
     * @param time
     * @return
     */
    private long getDateTime(String date, String time){
        if (time.contains(":") && time.lastIndexOf(":") == 5){
            try {
                String PATTERNS = "YYYY-MM-DD HH:mm:ss";
                return DateUtils.parseDate(date + " " + time, PATTERNS).getTime();
            }catch(ParseException pe){
                throw new IllegalStateException("自定义time时间转换错误：" +date + " " + time + ", " + pe.getMessage());
            }
        }else {
            throw new IllegalStateException("自定义time时间段格式错误,格式必需为HH:mm:ss");
        }
    }

}
