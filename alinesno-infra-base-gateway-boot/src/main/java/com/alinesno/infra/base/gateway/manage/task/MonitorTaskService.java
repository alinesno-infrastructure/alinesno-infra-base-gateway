package com.alinesno.infra.base.gateway.manage.task;

import com.alinesno.infra.base.gateway.core.entity.Monitor;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.service.MonitorService;
import com.alinesno.infra.base.gateway.core.service.RouteService;
import com.alinesno.infra.base.gateway.core.util.Constants;
import com.alinesno.infra.base.gateway.core.util.HttpUtils;
import com.alinesno.infra.base.gateway.core.util.RouteConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @description 执行监控网关路由服务
 *
 * @author  JL
 * @author  luoxiaodong
 *
 * @version v1.0.0
 */
@Slf4j
@Service
public class MonitorTaskService {

    @Resource
    private RouteService routeService;

    @Resource
    private MonitorService monitorService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RestTemplate restTemplate;

    private static ExecutorService executorService;
    static Map<String, String> headers = new HashMap<>();
    private final static String DEFAULT_CONTENT = "keepalive";
    static {
        headers.put("Keepalive", "flying-fish-gateway");
    }

    private final static int QUEUE_SIZE = 100;
    private final static int TASK_SIZE = 20;

    static {
        //corePoolSize:核心线程池大小
        //maximumPoolSize:最大线程池大小
        //keepAliveTime:线程最大空闲时间
        //unit:时间单位
        //workQueue:线程等待队列
        //threadFactory:线程创建工厂
        //handler:拒绝策略,
        //     AbortPolicy:直接抛出异常，阻止系统正常工作;
        //     CallerRunsPolicy:线程数量达到上限，把任务队列中的任务放在调用者线程当中运行;
        //     DiscardOledestPolicy:丢弃当前任务队列中最先被添加进去的，马上要被执行的那个任务，并尝试再次提交;
        //     DiscardPolicy:丢弃无法处理的任务，不予任何处理(业务场景中需允许任务的丢失);
        synchronized (MonitorTaskService.class) {
            executorService = new ThreadPoolExecutor(7,
                    20,
                    10L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(QUEUE_SIZE),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    /**
     * 异步执行监控任务
     */
    @Async
    public void executeMonitorTask(){
        List<Route> routeList;
        while(true){
            try{
                //监控网关路由服务,条件：网关状态为0正常，监控状态为：0正常或(2告警+0可重试)
                routeList = routeService.monitorRouteList();
                if (!CollectionUtils.isEmpty(routeList)){
                    monitorRoute(routeList.stream().collect(Collectors.toMap(Route::getId, r->r)));
                }
                //注意此处，需根据预估的真实队列总量计算，最大线程数按预估时间完成所有任务的总时长推算，在设定暂停时长，防止数据入队列太快，线程来不及处理，导致任务丢弃；
                TimeUnit.MILLISECONDS.sleep(10 * 1000);
            }catch(Exception e){
                log.error("执行监控任务失败！监控线程已退出...",e);
            }
        }
    }

    /**
     * 对网关路由发起请求，如果未正常响应，则认为接口不可用，并置为告警状态
     * @param dataMap
     */
    private void monitorRoute(Map<String,Route> dataMap){
        int i = 0;
        //每次最多获取TASK_SIZE次任务，防止队列出列过慢数据溢出
        while (TASK_SIZE > i){
            i ++;
            String routeId = (String)redisTemplate.opsForList().leftPop(RouteConstants.MONITOR_QUEUE_KEY);
            if (StringUtils.isBlank(routeId)){
                return;
            }
            Route route = dataMap.get(routeId);
            if (route == null) {
                return;
            }
            //执行线程池任务
            executorService.execute(() -> {
                String msg = null;
                String result = null;
                boolean isTimeout = true;
                int stripPrefix = route.getStripPrefix()==null?0:route.getStripPrefix();
                String path = route.getPath();
                String uri = route.getUri();
                String newUri = null;
                String method = StringUtils.isNotBlank(route.getMethod()) ? route.getMethod(): HttpUtils.HTTP_GET;
                //根据跳转规则拼装路径
                String newPath = "/"+ Arrays.stream(org.springframework.util.StringUtils.tokenizeToStringArray(path, "/"))
                        .skip(stripPrefix).collect(Collectors.joining("/"));
                newPath += (newPath.length() > 1 && path.endsWith("/") ? "/" : "");
                try {
                    if (uri.toLowerCase().startsWith("lb:")){
                        uri = uri.replaceAll("lb:", "http:").replaceAll("LB:", "http:");
                        newUri = uri +  newPath;
                        result = restTemplate.getForObject(newUri, String.class);
                    }else {
                        int index = uri.indexOf("//") + 2;
                        String http = uri.substring(0, index);
                        newUri = Arrays.stream(org.springframework.util.StringUtils.tokenizeToStringArray(uri.substring(index), "/"))
                                .limit(1).collect(Collectors.joining("/"));
                        newUri = http + newUri + newPath;
                        HttpUtils.HttpResponse response = HttpUtils.doHttp(newUri, method, headers, null, DEFAULT_CONTENT, -1);
                        if (response != null){
                            result = response.getContent();
                        }
                    }
                    isTimeout = false;
                    log.debug("request：" + newUri + ", result: " + result);
                } catch(IOException ioe){
                    msg = ioe.getMessage();
                } catch(Exception e){
                    log.error("执行监控任务服务异常，监控id :{}，监控名称 :{},请求地址：{},请求模式：{}, 错误消息：{}",
                            routeId, route.getName(), newUri, route.getMethod(), e.getMessage());
                    log.error("", e);
                    timeoutMonitor(routeId);
                    return;
                }
                //设置告警状态
                if (isTimeout){
                    log.error("执行监控任务访问异常，监控id :{}，监控名称 :{},请求地址：{},请求模式：{}, 超时时长：5000, 错误消息：{}",
                            routeId, route.getName(), newUri, route.getMethod(), msg);
                    timeoutMonitor(routeId);
                }
            });
        }
    }

    /**
     * 将当前网关路由服务的监控状态置为告警
     * @param routeId
     */
    public void timeoutMonitor(String routeId){
        Monitor monitor = monitorService.findById(routeId);
        //告警状态2
        monitor.setStatus(Constants.ALARM);
        //告警时间
        monitor.setAlarmTime(new Date());
        monitorService.update(monitor);
    }

}
