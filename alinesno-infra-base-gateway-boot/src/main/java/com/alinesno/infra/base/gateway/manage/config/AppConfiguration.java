package com.alinesno.infra.base.gateway.manage.config;

import com.alinesno.infra.common.web.adapter.sso.enable.EnableInfraSsoApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableInfraSsoApi
@EnableAsync
@EnableScheduling
@Configuration
public class AppConfiguration {
}
