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

    public static AnimationSegment scale(double startTime, double endTime, String easing, double from, double to) {
        return new AnimationSegment(startTime, endTime, "scale", from, to, easing);
    }

    public static AnimationSegment position(double startTime, double endTime, String easing, 
                                           double fromX, double fromY, double toX, double toY) {
        return new AnimationSegment(
            startTime, 
            endTime, 
            "position", 
            new PositionValue(fromX, fromY), 
            new PositionValue(toX, toY), 
            easing
        );
    }

    public static AnimationSegment cameraPosition(double startTime, double endTime, String easing,
                                                  double fromX, double fromY, double toX, double toY) {
        return new AnimationSegment(
            startTime,
            endTime,
            "cameraPosition",
            new PositionValue(fromX, fromY),
            new PositionValue(toX, toY),
            easing
        );
    }

    public static AnimationSegment zoom(double startTime, double endTime, String easing, double from, double to) {
        return new AnimationSegment(startTime, endTime, "zoom", from, to, easing);
    }

    public static AnimationSegment speed(double startTime, double endTime, String easing, double from, double to) {
        return new AnimationSegment(startTime, endTime, "speed", from, to, easing);
    }

    public record PositionValue(double x, double y) {
    }
}

