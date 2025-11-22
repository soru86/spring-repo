package com.stellerit.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Rewards Service.
 * This service calculates reward points for customers based on their transactions.
 * 
 * @author Steller IT Team
 * @version 1.0.0
 */
@SpringBootApplication
public class RewardsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RewardsServiceApplication.class, args);
    }
}

