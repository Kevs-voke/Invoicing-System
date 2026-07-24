package com.gkev.InvoicingSystem.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


    @Configuration
    public class CorsGlobalConfiguration {

        @Bean
        public CorsWebFilter corsWebFilter() {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOrigins(List.of(
                    "http://localhost:5173",
                    "https://imarabillers.onrender.com"
            ));
            corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfig.setAllowedHeaders(List.of("*"));
            corsConfig.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfig);

            return new CorsWebFilter(source);
        }
    }