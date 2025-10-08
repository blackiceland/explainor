package com.dev.explainor.genesis.renderer.domain;

import java.util.List;

public record FinalTimeline(
    Canvas canvas,
    double totalDuration,
    List<TimelineEvent> timeline,
    List<CameraEvent> camera
) {
}
