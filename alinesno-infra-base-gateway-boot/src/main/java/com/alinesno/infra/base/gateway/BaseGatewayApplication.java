package com.alinesno.infra.base.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author  luoxiaodong
 * @version v1.0.0
 */
@EnableAsync
@EnableScheduling
@Slf4j
@SpringBootApplication
public class BaseGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseGatewayApplication.class, args);
	}

}