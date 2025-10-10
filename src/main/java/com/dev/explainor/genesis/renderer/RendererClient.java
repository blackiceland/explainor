package com.dev.explainor.genesis.renderer;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RendererClient {

    private static final Logger log = LoggerFactory.getLogger(RendererClient.class);

    private final WebClient webClient;

    public RendererClient(@Qualifier("rendererWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JsonNode> renderVideo(FinalTimelineV1 timeline) {
        return this.webClient.post()
                .uri("/render")
                .bodyValue(timeline)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Renderer service error: {}", errorBody);
                            return Mono.error(new RuntimeException("Renderer service failed: " + errorBody));
                        })
                )
                .bodyToMono(JsonNode.class);
    }
}
