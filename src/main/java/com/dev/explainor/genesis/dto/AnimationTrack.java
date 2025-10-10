package com.dev.explainor.genesis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AnimationTrack(
    @JsonProperty("id") String id,
    @JsonProperty("type") String type,
    @JsonProperty("targetId") String targetId,
    @JsonProperty("segments") List<AnimationSegment> segments
) {
    public static AnimationTrack of(String id, String type, String targetId, List<AnimationSegment> segments) {
        return new AnimationTrack(id, type, targetId, segments);
    }

    public static AnimationTrack nodeTrack(String targetId, List<AnimationSegment> segments) {
        return new AnimationTrack("track-node-" + targetId, "node", targetId, segments);
    }

    public static AnimationTrack edgeTrack(String targetId, List<AnimationSegment> segments) {
        return new AnimationTrack("track-edge-" + targetId, "edge", targetId, segments);
    }

    public static AnimationTrack particleTrack(String targetId, List<AnimationSegment> segments) {
        return new AnimationTrack("track-particle-" + targetId, "particle", targetId, segments);
    }

    public static AnimationTrack cameraTrack(List<AnimationSegment> segments) {
        return new AnimationTrack("track-camera-main", "camera", "main-camera", segments);
    }
}

