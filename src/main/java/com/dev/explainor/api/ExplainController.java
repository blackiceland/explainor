package com.dev.explainor.api;

import com.dev.explainor.api.dto.ExplainRequest;
import com.dev.explainor.bridge.LlmBridge;
import com.dev.explainor.renderer.RendererClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ExplainController {
    private final LlmBridge llmBridge;
    private final RendererClient rendererClient;

    public ExplainController(LlmBridge llmBridge, RendererClient rendererClient) {
        this.llmBridge = llmBridge;
        this.rendererClient = rendererClient;
    }

    @PostMapping(value = "/explain", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JsonNode>> explain(@RequestBody ExplainRequest request) {
        return llmBridge.getAnimationScenario(request.getPrompt())
                .flatMap(rendererClient::renderVideo)
                .map(ResponseEntity::ok);
    }
}
