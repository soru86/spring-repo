package com.stellerit.rewards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI/Swagger documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rewardsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rewards Service API")
                        .description("REST API for calculating customer reward points based on transactions. " +
                                   "Reward points are calculated as follows: " +
                                   "For every dollar spent over $50, the customer receives 1 point. " +
                                   "For every dollar spent over $100, the customer receives an additional point.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Steller IT Team")
                                .email("support@stellerit.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

