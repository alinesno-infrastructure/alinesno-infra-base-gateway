package com.alinesno.infra.base.gateway;

import com.alinesno.infra.base.gateway.manage.task.MonitorTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description 动态路由配置管理
 * @author jianglong
 * @author luoxiaodong
 * @date 2020/05/27
 * @version v1.0.0
 */
@EnableAsync
@EnableScheduling
@Slf4j
@SpringBootApplication
public class BaseGatewayApplication {

	@Resource
	private MonitorTaskService monitorTaskService;

	public static void main(String[] args) {
		SpringApplication.run(BaseGatewayApplication.class, args);
	}

	/**
	 * 执行监控程序
	 */
//	@PostConstruct
	public void runMonitor(){
		log.info("运行网关路由监控任务...");
		monitorTaskService.executeMonitorTask();
	}

}