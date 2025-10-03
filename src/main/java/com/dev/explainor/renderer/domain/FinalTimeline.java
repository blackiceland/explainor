package com.dev.explainor.renderer.domain;

import java.util.List;

public record FinalTimeline(
    Canvas canvas,
    double totalDuration,
    List<TimelineEvent> timeline
) {
}
