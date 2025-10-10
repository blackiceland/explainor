package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConnectEntitiesParams(
    @JsonProperty("from") String from,
    @JsonProperty("to") String to,
    @JsonProperty("label") String label,
    @JsonProperty("lineStyle") String lineStyle
) {
}

