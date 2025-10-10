package com.dev.explainor.genesis.layout.model;

public record Viewport(
    double centerX,
    double centerY,
    double zoom
) {
    public static Viewport standard(double canvasWidth, double canvasHeight) {
        return new Viewport(canvasWidth / 2, canvasHeight / 2, 1.0);
    }
}

