package com.example.hotalproject;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI employeeApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SWER313 Hotel API")
                        .version("v1")
                        .description("Hotel service API for pagination, sorting, and filtering lab."));
    }
}