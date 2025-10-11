package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FocusOnParams(
    @JsonProperty("target") String target,
    @JsonProperty("area") String area,
    @JsonProperty("scale") Double scale,
    @JsonProperty("duration") Double duration,
    @JsonProperty("zoom") Double zoom,
    @JsonProperty("speed") Double speed
) {
    public double durationOrDefault(double defaultValue) {
        return duration != null ? duration : defaultValue;
    }

    public double zoomOrDefault(double defaultValue) {
        return zoom != null ? zoom : defaultValue;
    }

    public double speedOrDefault(double defaultValue) {
        return speed != null ? speed : defaultValue;
    }
}

