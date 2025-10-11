package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.Stage;
import com.dev.explainor.genesis.layout.model.LayoutResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimelineFactory {

    private static final int DEFAULT_CANVAS_WIDTH = 1280;
    private static final int DEFAULT_CANVAS_HEIGHT = 720;

    private final StylingService stylingService;

    public TimelineFactory(StylingService stylingService) {
        this.stylingService = stylingService;
    }

    public FinalTimelineV1 createFrom(LayoutResult layoutResult, List<AnimationTrack> animationTracks) {
        Stage stage = new Stage(
            DEFAULT_CANVAS_WIDTH,
            DEFAULT_CANVAS_HEIGHT
        );

        List<com.dev.explainor.genesis.dto.TimelineNode> nodes = stylingService.toTimelineNodes(layoutResult.nodes());
        List<com.dev.explainor.genesis.dto.TimelineEdge> edges = stylingService.toTimelineEdges(layoutResult.edges());

        return FinalTimelineV1.create(stage, nodes, edges, animationTracks);
    }
}
