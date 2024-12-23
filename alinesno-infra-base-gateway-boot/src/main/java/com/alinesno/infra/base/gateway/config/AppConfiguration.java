package com.alinesno.infra.base.gateway.config;

import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableInfraSsoApi
@EnableAsync
@ForestScan({
        "com.alinesno.infra.common.web.adapter.base.consumer" ,
        "com.alinesno.infra.base.gateway.core.consumer"
})
@EnableScheduling
@Configuration
public class AppConfiguration {
}
