package com.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig {

    @Value("${dashboard.thread-pool.core-size:10}")
    private int coreSize;

    @Value("${dashboard.thread-pool.max-size:20}")
    private int maxSize;

    @Value("${dashboard.thread-pool.queue-capacity:50}")
    private int queueCapacity;

    @Value("${dashboard.thread-pool.thread-name-prefix:dashboard-widget-}")
    private String threadNamePrefix;

    /**
     * Dedicated thread pool for parallel widget loading.
     * Sized independently from the main Tomcat request threads.
     */
    @Bean(name = "dashboardWidgetExecutor")
    public Executor dashboardWidgetExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                r -> {
                    Thread t = new Thread(r);
                    t.setName(threadNamePrefix + t.getId());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // fallback: run on caller thread
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
