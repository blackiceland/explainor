package com.dev.explainor.genesis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnimationSegment(
    @JsonProperty("t0") double startTime,
    @JsonProperty("t1") double endTime,
    @JsonProperty("property") String property,
    @JsonProperty("from") Object fromValue,
    @JsonProperty("to") Object toValue,
    @JsonProperty("easing") String easing
) {
    public static AnimationSegment of(double startTime, double endTime, String property, Object fromValue, Object toValue, String easing) {
        return new AnimationSegment(startTime, endTime, property, fromValue, toValue, easing);
    }

    public static AnimationSegment opacity(double startTime, double endTime, String easing) {
        return new AnimationSegment(startTime, endTime, "opacity", 0.0, 1.0, easing);
    }

    public static AnimationSegment scale(double startTime, double endTime, String easing) {
        return new AnimationSegment(startTime, endTime, "scale", 0.0, 1.0, easing);
    }

    public double duration() {
        return endTime - startTime;
    }
}

