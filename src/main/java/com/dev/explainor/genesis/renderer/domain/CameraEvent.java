package com.dev.explainor.genesis.renderer.domain;

public record CameraEvent(
    String type,
    double time,
    double duration,
    CameraTarget to
) {
    public record CameraTarget(
        Double x,
        Double y,
        Double scale
    ) {}
}

