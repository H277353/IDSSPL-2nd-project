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
import java.util.concurrent.ThreadPoolExecutor;

//@Configuration
//@EnableAsync
//public class AsyncConfig {
//
//    //----- for razorpay
//    @Bean("razorpayNotificationExecutor")
//    public Executor razorpayNotificationExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(4);
//        executor.setMaxPoolSize(8);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("razorpay-notify-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean("payoutExecutor")
//    public Executor payoutExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("payout-callback-");
//        executor.initialize();
//        return executor;
//    }
//
//    //----- NEW: for instant settlement (T+0) processing
//    @Bean("instantSettlementExecutor")
//    public Executor instantSettlementExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(15);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("instant-settle-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean("settlementExecutor")
//    public Executor settlementExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5);
//        executor.setMaxPoolSize(10);
//        executor.setQueueCapacity(100);
//        executor.setThreadNamePrefix("settlement-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean("merchantSettlementExecutor")
//    public Executor merchantSettlementExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(20);
//        executor.setQueueCapacity(200);
//        executor.setThreadNamePrefix("merchant-settlement-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean("franchiseSettlementExecutor")
//    public Executor franchiseSettlementExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3);
//        executor.setMaxPoolSize(8);
//        executor.setQueueCapacity(50);
//        executor.setThreadNamePrefix("franchise-settlement-");
//        executor.initialize();
//        return executor;
//    }
//}

@Configuration
@EnableAsync
public class AsyncConfig {

    // Critical path: Instant settlements + notifications (time-sensitive)
    @Bean("criticalAsyncExecutor")
    public Executor criticalAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("critical-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    // Bulk processing: Manual settlements + franchise processing (can wait)
    @Bean("bulkAsyncExecutor")
    public Executor bulkAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("bulk-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    // Map existing beans
    @Bean("razorpayNotificationExecutor")
    public Executor razorpayNotificationExecutor() {
        return criticalAsyncExecutor();
    }

    @Bean("payoutExecutor")
    public Executor payoutExecutor() {
        return criticalAsyncExecutor();
    }

    @Bean("instantSettlementExecutor")
    public Executor instantSettlementExecutor() {
        return criticalAsyncExecutor();
    }

    @Bean("settlementExecutor")
    public Executor settlementExecutor() {
        return bulkAsyncExecutor();
    }

    @Bean("merchantSettlementExecutor")
    public Executor merchantSettlementExecutor() {
        return bulkAsyncExecutor();
    }

    @Bean("franchiseSettlementExecutor")
    public Executor franchiseSettlementExecutor() {
        return bulkAsyncExecutor();
    }
}