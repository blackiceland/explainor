package com.dev.explainor.renderer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RendererClient {

    private final WebClient webClient;

    public RendererClient(WebClient.Builder webClientBuilder, @Value("${renderer.url}") String rendererUrl) {
        this.webClient = webClientBuilder.baseUrl(rendererUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<JsonNode> renderVideo(JsonNode scenario) {
        return this.webClient.post()
                .uri("/render")
                .bodyValue(scenario)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
