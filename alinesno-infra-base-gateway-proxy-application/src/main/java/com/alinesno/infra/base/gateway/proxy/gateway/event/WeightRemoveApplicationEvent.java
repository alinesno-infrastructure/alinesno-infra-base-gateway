package com.alinesno.infra.base.gateway.proxy.gateway.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @description 负载均衡网关权重移除事件，发布后触发CustomWeightCalculatorWebFilter过滤器中监听方法
 * @author  JL
 * @date 2021/10/12
 * @version 1.0.0
 */
@Getter
public class WeightRemoveApplicationEvent extends ApplicationEvent {

    /**
     * 权重分组名称
     */
    private String group;

    public WeightRemoveApplicationEvent(Object source, String group) {
        super(source);
        this.group = group;
    }

}
