package com.alinesno.infra.base.gateway.proxy.gateway.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Description 负载均衡网关权重移除事件，发布后触发CustomWeightCalculatorWebFilter过滤器中监听方法
 * @Author JL
 * @Date 2021/10/12
 * @Version V1.0
 */
public class WeightRemoveApplicationEvent extends ApplicationEvent {

    /**
     * 权重分组名称
     */
    private String group;

    public WeightRemoveApplicationEvent(Object source, String group) {
        super(source);
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

}
