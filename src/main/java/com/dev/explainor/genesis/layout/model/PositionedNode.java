package com.dev.explainor.genesis.layout.model;

public record PositionedNode(
    String id,
    String label,
    String icon,
    double x,
    double y,
    double width,
    double height
) {
    public BoundingBox boundingBox() {
        return BoundingBox.fromCenter(x, y, width, height);
    }
}

