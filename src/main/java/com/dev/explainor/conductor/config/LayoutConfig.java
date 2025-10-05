package com.dev.explainor.conductor.config;

import com.dev.explainor.conductor.layout.GraphBasedLayoutManager;
import com.dev.explainor.conductor.layout.LayoutManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LayoutConfig {

    @Bean
    @Primary
    public LayoutManager layoutManager(GraphBasedLayoutManager graphBasedLayoutManager) {
        return graphBasedLayoutManager;
    }
}

