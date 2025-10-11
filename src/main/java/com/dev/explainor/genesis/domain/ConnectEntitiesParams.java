package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record ConnectEntitiesParams(
    @JsonProperty("from") String from,
    @JsonProperty("to") String to,
    @JsonProperty("label") String label,
    @JsonProperty("lineStyle") String lineStyle,
    @JsonProperty("dashed") Boolean dashed,
    @JsonProperty("arrowStyle") String arrowStyle
) {
    public String lineStyleOrDefault(String defaultStyle) {
        return lineStyle != null && !lineStyle.isBlank() ? lineStyle : defaultStyle;
    }

    public String arrowStyleOrDefault(String defaultStyle) {
        return arrowStyle != null && !arrowStyle.isBlank() ? arrowStyle : defaultStyle;
    }

    public boolean isDashed() {
        return Boolean.TRUE.equals(dashed);
    }
}

