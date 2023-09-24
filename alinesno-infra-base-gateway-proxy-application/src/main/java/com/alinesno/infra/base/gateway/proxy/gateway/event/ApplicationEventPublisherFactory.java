package com.alinesno.infra.base.gateway.proxy.gateway.event;

import com.alinesno.infra.base.gateway.formwork.util.RouteConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @Description 发布事件，触发监听事件的方法
 * @Author JL
 * @Date 2020/06/02
 * @Version V1.0
 */
@Lazy
@Slf4j
@Component
public class ApplicationEventPublisherFactory implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 刷新路由，通过spring的事件监听机制，发布事件，触发监听方法的执行
     * （已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
     * @return
     */
    @Deprecated
    public void publisherEvent(String type){
        if (StringUtils.equals(type, RouteConstants.IP)) {
            this.publisher.publishEvent(new DataIpApplicationEvent(this));
        }else if (StringUtils.equals(type, RouteConstants.CLIENT_ID)){
            this.publisher.publishEvent(new DataClientApplicationEvent(this));
        } else if (StringUtils.equals(type, RouteConstants.ROUTE)) {
            this.publisher.publishEvent(new DataRouteApplicationEvent(this));
        } else {
            throw new IllegalArgumentException("发布事件错误，参数格式不正确");
        }
    }

    /**
     * 发布清除权重分组WeightRemoveApplicationEvent事件，会触发MyWeightCalculatorWebFilter过滤器监听并从权重集合中移除group
     * @param group
     */
    public void publisherWeightRemoveEvent(String group){
        this.publisher.publishEvent(new WeightRemoveApplicationEvent(this, group));
    }

    /**
     * 刷新网关路由
     */
    public void refreshRoutesEvent(){
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

}
