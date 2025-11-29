package com.project2.ism.WebConfig;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
//@Configuration
//@EnableAsync
//public class AsyncConfig {
//    @Bean(name = "settlementExecutor")
//    public Executor settlementExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(4);
//        executor.setMaxPoolSize(8);
//        executor.setQueueCapacity(500);
//        executor.setThreadNamePrefix("settle-");
//        executor.initialize();
//        return executor;
//    }
//}




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    //----- for razorpay
    @Bean("razorpayNotificationExecutor")
    public Executor razorpayNotificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("razorpay-notify-");
        executor.initialize();
        return executor;
    }

    @Bean("payoutExecutor")
    public Executor payoutExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("payout-callback-");
        executor.initialize();
        return executor;
    }

    //----- NEW: for instant settlement (T+0) processing
    @Bean("instantSettlementExecutor")
    public Executor instantSettlementExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("instant-settle-");
        executor.initialize();
        return executor;
    }

    @Bean("settlementExecutor")
    public Executor settlementExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("settlement-");
        executor.initialize();
        return executor;
    }

    @Bean("merchantSettlementExecutor")
    public Executor merchantSettlementExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("merchant-settlement-");
        executor.initialize();
        return executor;
    }

    @Bean("franchiseSettlementExecutor")
    public Executor franchiseSettlementExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("franchise-settlement-");
        executor.initialize();
        return executor;
    }
}