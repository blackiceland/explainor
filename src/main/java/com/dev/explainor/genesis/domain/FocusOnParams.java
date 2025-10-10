package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FocusOnParams(
    @JsonProperty("target") String target, 
    @JsonProperty("area") String area, 
    @JsonProperty("scale") Double scale
) {
}

