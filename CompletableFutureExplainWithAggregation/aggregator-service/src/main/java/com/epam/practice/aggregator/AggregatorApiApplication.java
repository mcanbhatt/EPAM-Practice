package com.epam.practice.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AggregatorApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApiApplication.class, args);
    }
}
