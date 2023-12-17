package com.alinesno.infra.base.gateway.proxy.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * @description 创建自定义客户端事件
 * @author  JL
 * @author luoxiaodong
 * @date 2020/05/28
 * @version 1.0.0
 */
public class DataClientApplicationEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DataClientApplicationEvent(Object source) {
        super(source);
    }
}
