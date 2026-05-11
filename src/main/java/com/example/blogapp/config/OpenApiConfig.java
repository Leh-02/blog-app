package com.example.blogapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI blogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blog Platform API")
                        .description("REST backend for a blog platform with JWT authentication, PostgreSQL, file upload, cache and async processing")
                        .version("1.0.0")
                        .contact(new Contact().name("Student project"))
                        .license(new License().name("Educational use")));
    }
}
