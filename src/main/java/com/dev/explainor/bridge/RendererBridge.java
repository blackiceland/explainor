package com.dev.explainor.bridge;

import com.dev.explainor.model.RenderResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RendererBridge {

    private final WebClient webClient;
    private final String rendererUrl;

    public RendererBridge(WebClient webClient, @Value("${renderer.url}") String rendererUrl) {
        this.webClient = webClient;
        this.rendererUrl = rendererUrl;
    }

    public Mono<RenderResponse> callRenderer(JsonNode scenario) {
        return webClient.post()
                .uri(rendererUrl + "/render")
                .bodyValue(scenario)
                .retrieve()
                .bodyToMono(RenderResponse.class);
    }
}

