package com.example.ConnectUs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/v1/**")
                .allowedOrigins("http://127.0.0.1:5173")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);

        /*registry.addMapping("/api/v1/post/**")
                .allowedOrigins("http://127.0.0.1:5173")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);

        registry.addMapping("/api/v1/user/**")
                .allowedOrigins("http://127.0.0.1:5173")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);*/
    }

}
