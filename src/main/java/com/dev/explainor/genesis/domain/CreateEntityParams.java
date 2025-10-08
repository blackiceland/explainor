package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateEntityParams(
    String label,
    String icon,
    String shape,
    String positionHint
) {
    @JsonCreator
    public CreateEntityParams(
            @JsonProperty("label") String label,
            @JsonProperty("icon") String icon,
            @JsonProperty("shape") String shape,
            @JsonProperty("positionHint") String positionHint) {
        this.label = label;
        this.icon = icon;
        this.shape = shape == null ? "rectangle" : shape;
        this.positionHint = positionHint;
    }
}

