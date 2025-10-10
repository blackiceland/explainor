package com.dev.explainor.genesis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AnimateBehaviorParams(
    @JsonProperty("behavior") String behavior,
    @JsonProperty("from") String from,
    @JsonProperty("to") String to,
    @JsonProperty("duration") Double duration,
    @JsonProperty("speed") Double speed
) {
    public static AnimateBehaviorParams flow(String from, String to) {
        return new AnimateBehaviorParams("flow", from, to, null, null);
    }

    public static AnimateBehaviorParams orbit(String center, Double duration) {
        return new AnimateBehaviorParams("orbit", center, null, duration, null);
    }
}

