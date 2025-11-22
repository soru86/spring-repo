package com.cqrs.demo.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CommandServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommandServiceApplication.class, args);
    }
}

