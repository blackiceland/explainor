package com.dev.explainor.genesis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    @Bean
    public WebClient anthropicWebClient(@Value("${anthropic.api.key}") String apiKey) {
        return WebClient.builder()
                .baseUrl(ANTHROPIC_API_URL)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", ANTHROPIC_VERSION)
                .defaultHeader("content-type", "application/json")
                .build();
    }

    @Bean
    public WebClient rendererWebClient(@Value("${renderer.url}") String rendererUrl) {
        return WebClient.builder()
                .baseUrl(rendererUrl)
                .build();
    }
}

