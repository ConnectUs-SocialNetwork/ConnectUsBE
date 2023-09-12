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

        registry.addMapping("/api/v1/auth/**")		// dozvoljava cross-origin zahteve ka navedenim putanjama
                .allowedOrigins("http://127.0.0.1:5173")	// postavice Access-Control-Allow-Origin header u preflight zahtev
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);		// definise u sekundama koliko dugo se preflight response cuva u browseru


    }

}
