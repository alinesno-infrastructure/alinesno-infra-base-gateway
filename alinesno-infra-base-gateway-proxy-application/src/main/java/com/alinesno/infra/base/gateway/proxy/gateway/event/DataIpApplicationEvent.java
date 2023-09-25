package com.alinesno.infra.base.gateway.proxy.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * @description 创建自定义IP事件（已过时，启用nacos配置监听事件，参见：NacosConfigRefreshEventListener）
 * @author  JL
 * @date 2020/05/28
 * @version 1.0.0
 */
@Deprecated
public class DataIpApplicationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataIpApplicationEvent(Object source) {
        super(source);
    }
}
