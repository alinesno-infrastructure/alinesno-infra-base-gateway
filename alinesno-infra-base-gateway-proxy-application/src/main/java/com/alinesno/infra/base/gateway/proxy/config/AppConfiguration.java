package com.alinesno.infra.base.gateway.proxy.config;

import com.alinesno.infra.base.gateway.core.config.ApplicationContextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories(basePackages = "com.alinesno.infra.base.gateway.core.dao")
@EnableScheduling
@Configuration
public class AppConfiguration {

    @Bean
    public ApplicationContextProvider getApplicationContextProvider(){
        return new ApplicationContextProvider() ;
    }

}