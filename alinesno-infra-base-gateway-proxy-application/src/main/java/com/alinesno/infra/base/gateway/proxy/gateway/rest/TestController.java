package com.alinesno.infra.base.gateway.proxy.gateway.rest;//package com.flying.fish.gateway.rest;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//
//import brave.Span;
//import brave.Tracer;
//import lombok.extern.log4j.Log4j2;
//import reactor.core.publisher.Mono;
//
///**
// * @Description
// * @Author jianglong
// * @Date 2020/06/02
// * @Version V1.0
// */
//@Log4j2
//@RestController
//public class TestController {
//    @Autowired
//    private TestService testService;
//
//    @RequestMapping("/test")
//    @CrossOrigin(origins = {"http://localhost:8771", "null"})
//    public Mono<String> test() {
//        log.info("test started");
//        return Mono.fromFuture(testService.simulateIOTest());
//    }
//
//    @Service
//    public static class TestService {
//        @Autowired
//        private Tracer tracer;
//        ThreadFactory build = (new ThreadFactoryBuilder()).setNameFormat("test_service_executor-%d").build();
//        private ExecutorService executorService = new ThreadPoolExecutor(50, 50, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(131072), build, new ThreadPoolExecutor.AbortPolicy());
//
//        /**
//         * CompletableFuture线程池管理，并添加日志追踪
//         * @return
//         */
//        public CompletableFuture<String> simulateIOTest() {
//            Span span = tracer.currentSpan();
//            return CompletableFuture.supplyAsync(() -> {
//                try (Tracer.SpanInScope cleared = tracer.withSpanInScope(span)) {
//                    //simulate io
//                    log.info("simulate start");
//                    TimeUnit.SECONDS.sleep(5);
//                    log.info("simulate end");
//                    return "hello";
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }, executorService);
//        }
//    }
//}