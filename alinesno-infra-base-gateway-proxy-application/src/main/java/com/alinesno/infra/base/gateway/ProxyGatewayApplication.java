package com.alinesno.infra.base.gateway;

import com.alinesno.infra.base.gateway.formwork.config.ApplicationContextProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 集成一个Java开发示例工具
 * @author  luoxiaodong
 * @version 1.0.0
 */
@EnableJpaRepositories
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ProxyGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyGatewayApplication.class, args);
	}

	@Bean
	public ApplicationContextProvider getApplicationContextProvider(){
		return new ApplicationContextProvider() ;
	}

}