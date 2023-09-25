package com.alinesno.infra.base.gateway.proxy.gateway.listener;

import com.alinesno.infra.base.gateway.proxy.gateway.service.ConfigRefreshService;
import jakarta.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description 实现自定义nacos配置动态更新监听事件（模仿RefreshEventListener类写法，当nacos配置发生变更，会主动推送到注册配置应用，并通过RefreshEvent事件触发相关监听器事件）
 * @author  JL
 * @date 2021/09/16
 * @version 1.0.0
 */
@Component
public class NacosConfigRefreshEventListener implements SmartApplicationListener {

    private static Log log = LogFactory.getLog(NacosConfigRefreshEventListener.class);

    private ContextRefresher refresh;

//    @Autowired
//    private NacosConfigProperties configProperties;

    @Autowired(required = false)
    private List<PropertySourceLocator> propertySourceLocators = new ArrayList<>();

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Resource
    private ConfigRefreshService configRefreshService;

    private AtomicBoolean ready = new AtomicBoolean(false);

    public NacosConfigRefreshEventListener(ContextRefresher refresh) {
        this.refresh = refresh;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.isAssignableFrom(eventType)
                || RefreshEvent.class.isAssignableFrom(eventType);
    }

    /**
     * 触发监听事件核心方法
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            handle((ApplicationReadyEvent) event);
        }
        else if (event instanceof RefreshEvent) {
            handle((RefreshEvent) event);
        }
    }

    public void handle(ApplicationReadyEvent event) {
        this.ready.compareAndSet(false, true);
    }

    /**
     * 发布事件后，执行相应动作
     * @param event
     */
    public void handle(RefreshEvent event) {
        // don't handle events before app is ready
        if (this.ready.get()) {
            log.debug("Event received " + event.getEventDesc());
            Set<String> keys = this.refresh.refresh();
            log.info("Refresh keys changed: " + keys);
            String gatewayConfig = null;
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            // 只处理nacos有关的PropertySource配置更新属性
            // 当nacos配置变更推送到应用后，spring-cloud-context.jar中的PropertySourceBootstrapConfiguration.initialize()方法会被重新初始化，
            // 重新获取上下文中的配置，重新初始化覆盖所有配置到spring框架中，如同重启过程中加载所有配置一样；
            // 以下代码模仿PropertySourceBootstrapConfiguration.initialize()中的96行~111行逻辑，获取指定配置
            for (PropertySourceLocator locator : propertySourceLocators){
                Collection<PropertySource<?>> source = locator.locateCollection(environment);
                if (source == null || source.size() == 0) {
                    continue;
                }
//                if (locator instanceof NacosPropertySourceLocator){
//                    for (PropertySource<?> p : source) {
//                        //只取nacos中的变更配置
//                        if (p instanceof NacosPropertySource) {
//                            NacosPropertySource enumerable = (NacosPropertySource) p;
//                            if (enumerable.getPropertyNames().length > 0){
//                                gatewayConfig = (String) enumerable.getProperty("gateway");
//                                break;
//                            }
//                        } else {
//                            gatewayConfig = (String) p.getProperty("gateway");
//                        }
//                    }
//                    break;
//                }
            }
            // 更新网关路由
//            if (StringUtils.isNotBlank(gatewayConfig)){
//                log.info("Refresh gatewayConfig changed: " + gatewayConfig);
//                configRefreshService.setGatewayConfig(gatewayConfig);
//                log.info("Refresh gatewayConfig changed success!");
//            }
        }
    }

}
