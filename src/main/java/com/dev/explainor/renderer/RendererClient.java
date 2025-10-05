package com.dev.explainor.renderer;

import com.dev.explainor.renderer.domain.FinalTimeline;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RendererClient {

    private static final Logger log = LoggerFactory.getLogger(RendererClient.class);

    private final WebClient webClient;

    public RendererClient(WebClient.Builder webClientBuilder, @Value("${renderer.url}") String rendererUrl) {
        this.webClient = webClientBuilder.baseUrl(rendererUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Sends a timeline to the Remotion renderer service for video generation.
     *
     * @param timeline the complete animation timeline
     * @return Mono containing the render response with video URL
     * @throws RuntimeException if the renderer service returns an error
     */
    public Mono<JsonNode> renderVideo(FinalTimeline timeline) {
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
