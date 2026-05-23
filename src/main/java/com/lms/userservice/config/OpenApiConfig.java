package com.lms.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LMS User Service API")
                        .version("v1")
                        .description("Initial API for user, role, and organization management in the LMS platform.")
                        .contact(new Contact().name("LMS Learning Platform")));
    }
}
