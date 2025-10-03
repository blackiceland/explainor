package com.dev.explainor.bridge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sashirestela.anthropic.Anthropic;
import io.github.sashirestela.anthropic.domain.completion.CompletionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class LlmBridge {

    private final Anthropic anthropic;
    private final String systemPrompt;
    private final ObjectMapper objectMapper;

    public LlmBridge(@Value("${anthropic.api.key}") String apiKey,
                     @Value("classpath:system_prompt.txt") Resource systemPromptResource) throws IOException {
        this.anthropic = Anthropic.builder()
                .apiKey(apiKey)
                .build();
        this.systemPrompt = asString(systemPromptResource);
        this.objectMapper = new ObjectMapper();
    }

    public Mono<JsonNode> getAnimationScenario(String prompt) {
        String fullPrompt = "\n\nHuman: " + systemPrompt + "\n" + prompt + "\n\nAssistant:";
        CompletionRequest request = CompletionRequest.builder()
                .prompt(fullPrompt)
                .model("claude-3-sonnet-20240229")
                .maxTokensToSample(4096)
                .temperature(0.0)
                .build();

        return anthropic.completions().create(request)
                .map(response -> {
                    try {
                        return objectMapper.readTree(response.getCompletion());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse LLM response", e);
                    }
                });
    }

    private String asString(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}

