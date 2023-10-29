package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
public class CreatingAsynchronousMethodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreatingAsynchronousMethodsApplication.class, args);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 기본적으로 실행을 대기하고 있는 쓰레드의 개수
        executor.setMaxPoolSize(2); // 동시에 실행할 수 있는 쓰레드의 최대 개수
        executor.setQueueCapacity(500); // 대기중인 작업큐의 최대 개수
        executor.setThreadNamePrefix("GithubLookup-"); // 쓰레드 이름 접두어
        executor.initialize();
        return executor;
    }

}
