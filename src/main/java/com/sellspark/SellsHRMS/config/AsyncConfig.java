package com.sellspark.SellsHRMS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${app.async.pool.core-size:4}")
    private int corePoolSize;

    @Value("${app.async.pool.max-size:10}")
    private int maxPoolSize;

    @Value("${app.async.pool.queue-capacity:50}")
    private int queueCapacity;

    @Bean(name = "fileProcessingExecutor")
    public Executor fileProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("file-proc-");
        executor.initialize();
        return executor;
    }
}
