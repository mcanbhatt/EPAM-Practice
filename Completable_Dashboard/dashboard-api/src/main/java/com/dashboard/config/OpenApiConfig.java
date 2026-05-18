package com.dashboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dashboardOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dashboard API")
                        .description("Parallel widget loading via CompletableFuture — Spring Boot")
                        .version("1.0.0")
                        .contact(new Contact().name("Dashboard Team").email("team@dashboard.com"))
                        .license(new License().name("MIT")));
    }
}
