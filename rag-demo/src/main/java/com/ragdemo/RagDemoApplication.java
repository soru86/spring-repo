package com.ragdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RagDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RagDemoApplication.class, args);
    }
}

