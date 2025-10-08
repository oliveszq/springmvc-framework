package com.example.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    //配置线程池的方法
    @Bean
    public TaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置线程池的核心线程数为10
        executor.setCorePoolSize(10);
        //设置线程池的最大线程数为20
        executor.setMaxPoolSize(20);
        //设置线程池的队列容量为20
        executor.setQueueCapacity(20);
        // 设置线程的存活时间为 60 秒，即当线程空闲时间超过 60 秒时，该线程将被回收。
        executor.setKeepAliveSeconds(60);
        //设置线程的名称前缀
        executor.setThreadNamePrefix("async-task-thread-");
        return executor;
    }
}