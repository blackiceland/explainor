package com.dev.explainor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // In the future, we can customize the ObjectMapper here if needed
        return new ObjectMapper();
    }
}
