package com.dev.explainor.api;

import com.dev.explainor.bridge.LlmBridge;
import com.dev.explainor.bridge.RendererBridge;
import com.dev.explainor.model.GenerationRequest;
import com.dev.explainor.model.RenderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class GenerationController {

    private final LlmBridge llmBridge;
    private final RendererBridge rendererBridge;

    public GenerationController(LlmBridge llmBridge, RendererBridge rendererBridge) {
        this.llmBridge = llmBridge;
        this.rendererBridge = rendererBridge;
    }

    @PostMapping("/generate-video")
    public Mono<RenderResponse> generateVideo(@RequestBody GenerationRequest request) {
        return llmBridge.getAnimationScenario(request.getPrompt())
                .flatMap(rendererBridge::callRenderer);
    }
}
