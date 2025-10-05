package com.dev.explainor.api;

import com.dev.explainor.api.dto.ExplainRequest;
import com.dev.explainor.bridge.LlmBridge;
import com.dev.explainor.conductor.service.ConductorService;
import com.dev.explainor.renderer.RendererClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
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
    private final ConductorService conductorService;

    public ExplainController(LlmBridge llmBridge, RendererClient rendererClient, ConductorService conductorService) {
        this.llmBridge = llmBridge;
        this.rendererClient = rendererClient;
        this.conductorService = conductorService;
    }

    /**
     * Generates an explainer video from a user prompt.
     *
     * @param request the user's request containing the prompt
     * @return Mono containing the render response with video URL
     */
    @PostMapping(value = "/explain", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<JsonNode>> explain(@RequestBody @Valid ExplainRequest request) {
        return llmBridge.getAnimationStoryboard(request.getPrompt())
            .map(conductorService::generateTimeline)
            .flatMap(rendererClient::renderVideo)
            .map(ResponseEntity::ok);
    }
}
