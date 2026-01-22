package com.client.ws.rasmooplus.configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WS Rasmoo Plus API")
                        .version("0.0.1")
                        .description("API para atender o cliente Rasmoo Plus")
                        .license(new License().name("Rasmoo cursos de tecnologia")));
    }
}