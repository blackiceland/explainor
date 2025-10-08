package com.dev.explainor.genesis.layout.model;

public record LayoutConstraints(
    int canvasWidth,
    int canvasHeight
) {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;

    public static LayoutConstraints standard() {
        return new LayoutConstraints(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static LayoutConstraints create(int width, int height) {
        return new LayoutConstraints(width, height);
    }
}

