package com.dev.explainor.genesis.controller;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.renderer.RendererClient;
import com.dev.explainor.genesis.service.GenesisConductorService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/genesis")
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    private final GenesisConductorService conductorService;
    private final RendererClient rendererClient;

    public VideoController(GenesisConductorService conductorService, RendererClient rendererClient) {
        this.conductorService = conductorService;
        this.rendererClient = rendererClient;
    }

    @PostMapping(value = "/render-video", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<JsonNode> renderVideo(@RequestBody StoryboardV1 storyboard) {
        log.info("Received video render request");
        
        return Mono.fromCallable(() -> {
            log.info("Generating timeline from storyboard...");
            FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
            log.info("Timeline generated successfully");
            return timeline;
        })
        .flatMap(timeline -> {
            log.info("Sending timeline to renderer service...");
            return rendererClient.renderVideo(timeline);
        })
        .doOnSuccess(response -> log.info("Video render completed: {}", response))
        .doOnError(error -> log.error("Video render failed", error));
    }
}

