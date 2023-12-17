package com.alinesno.infra.base.gateway;
 
import com.alinesno.infra.base.gateway.manage.task.MonitorTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MonitorRunner implements ApplicationRunner {

    @Resource
    private MonitorTaskService monitorTaskService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("运行网关路由监控任务...");
    }
}