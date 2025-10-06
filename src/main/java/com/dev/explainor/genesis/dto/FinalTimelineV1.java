package com.dev.explainor.genesis.dto;

import java.util.List;

public record FinalTimelineV1(
    String version,
    Stage stage,
    List<TimelineNode> nodes,
    List<TimelineEdge> edges
) {
    public static final String CURRENT_VERSION = "1.0.0";

    public static FinalTimelineV1 create(Stage stage, List<TimelineNode> nodes, List<TimelineEdge> edges) {
        return new FinalTimelineV1(CURRENT_VERSION, stage, nodes, edges);
    }
}

