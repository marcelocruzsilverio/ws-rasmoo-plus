package com.client.ws.rasmooplus.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi rasmooApi() {
        return GroupedOpenApi.builder()
                .group("rasmoo-plus")
                .displayName("Rasmoo Plus API - V0")
                .pathsToMatch("/**")  // Pega TODOS os endpoints
                .packagesToScan("com.client.ws.rasmooplus.controller")
                .build();
    }
}