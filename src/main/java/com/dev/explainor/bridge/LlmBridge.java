package com.dev.explainor.bridge;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.Storyboard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class LlmBridge {

    private static final Logger log = LoggerFactory.getLogger(LlmBridge.class);

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String MODEL_NAME = "claude-3-7-sonnet-20250219";

    private final WebClient webClient;
    private final String systemPrompt;
    private final ObjectMapper objectMapper;

    public LlmBridge(@Value("${anthropic.api.key}") String apiKey,
                     @Value("classpath:system_prompt_v2.txt") Resource systemPromptResource,
                     ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(ANTHROPIC_API_URL)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", ANTHROPIC_VERSION)
                .defaultHeader("content-type", "application/json")
                .build();
        try {
            this.systemPrompt = asString(systemPromptResource);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load system prompt from classpath", e);
        }
        this.objectMapper = objectMapper;
    }

    /**
     * Calls the LLM translator to generate a high-level storyboard from a user prompt.
     *
     * @param prompt the user's request for an explainer video
     * @return Mono containing the generated storyboard
     * @throws RuntimeException if the LLM API call fails or returns invalid data
     */
    public Mono<Storyboard> getAnimationStoryboard(String prompt) {
        Map<String, Object> requestBody = Map.of(
            "model", MODEL_NAME,
            "max_tokens", 4096,
            "temperature", 0.0,
            "system", systemPrompt,
            "messages", List.of(
                Map.of(
                    "role", "user",
                    "content", prompt
                )
            )
        );

        return webClient.post()
                .uri("/v1/messages")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                    response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Anthropic API error: {}", errorBody);
                            return Mono.error(new RuntimeException("Anthropic API returned error: " + errorBody));
                        })
                )
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    try {
                        log.info("Received response from Claude: {}", response);

                        if (!response.has("content") || response.get("content").isEmpty()) {
                            log.error("Invalid LLM response structure: {}", response);
                            throw new RuntimeException("Invalid LLM response: missing 'content' field");
                        }

                        JsonNode contentNode = response.get("content").get(0);
                        if (!contentNode.has("text")) {
                            log.error("Invalid LLM response structure: content item missing 'text' field: {}", response);
                            throw new RuntimeException("Invalid LLM response: content missing 'text' field");
                        }

                        String jsonResponse = contentNode.get("text").asText();
                        log.info("Claude generated storyboard JSON: {}", jsonResponse);

                        List<Command> commands = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
                        log.info("Successfully parsed {} commands from storyboard", commands.size());

                        return new Storyboard(commands);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse storyboard from LLM response: {}", response, e);
                        throw new RuntimeException("Failed to parse LLM storyboard response", e);
                    }
                });
    }

    private String asString(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
