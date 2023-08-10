package com.alinesno.infra.base.gateway.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 集成一个Java开发示例工具
 * @author luoxiaodong
 * @version 1.0.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ProxyGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyGatewayApplication.class, args);
	}

}