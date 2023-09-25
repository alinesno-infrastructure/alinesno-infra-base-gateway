package com.alinesno.infra.base.gateway.proxy.gateway.event;


import com.alinesno.infra.base.gateway.formwork.service.RegServerService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @description 监听DataClientApplicationEvent事件，并触发客户端数据重新加载（已过时）
 * @author  JL
 * @date 2020/05/28
 * @version 1.0.0
 */
@Slf4j
@Component
@Deprecated
public class DataClientApplicationEventListen {

    @Resource
    private RegServerService regServerService;

    /**
     * 监听事件刷新配置；
     * DataClientApplicationEvent发布后，即触发listenEvent事件方法；
     * （已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
     */
    @Deprecated
    @EventListener(classes = DataClientApplicationEvent.class)
    public void listenEvent() {
        // Todo 停止使用，请用InitClientService类initLoadClient()方法
        //initLoadClient();
    }

    /**
     * 第一次初始化加载
     */
    @Deprecated
    //@PostConstruct
    public void initLoadClient(){
        /*
        List list = regServerService.allRegClientList();
        ClientIdCache.clear();
        RegIpListCache.clear();
        int size = 0;
        if (!CollectionUtils.isEmpty(list)){
            size = list.size();
            Iterator iterator = list.iterator();
            String routeId ;
            String ip;
            String id;
            Object [] object;
            List<String> ips;
            List<String> ids;
            while (iterator.hasNext()){
                object = (Object[]) iterator.next();
                routeId = String.valueOf(object[0]);
                //添加网关路由注册的客户端ID
                id = String.valueOf(object[1]);
                ids = (List<String>) ClientIdCache.get(routeId);
                if (CollectionUtils.isEmpty(ids)) {
                    ids = new ArrayList<>();
                }
                ids.add(id);
                ClientIdCache.put(routeId, ids);

                //添加网关路由注册的客户端IP
                ip = String.valueOf(object[2]);
                ips = (List<String>) RegIpListCache.get(routeId);
                if (CollectionUtils.isEmpty(ips)) {
                    ips = new ArrayList<>();
                }
                ips.add(ip);
                RegIpListCache.put(routeId, ips);
            }
        }
        log.info("监听到客户端配置发生变更，重新加载客户端配置共{}条", size);
        */
    }

}
