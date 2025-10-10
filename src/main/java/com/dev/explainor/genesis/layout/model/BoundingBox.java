package com.dev.explainor.genesis.layout.model;

public record BoundingBox(
    double x,
    double y,
    double width,
    double height
) {
    public double centerX() {
        return x + width / 2;
    }
    
    public double centerY() {
        return y + height / 2;
    }
    
    public static BoundingBox fromCenter(double centerX, double centerY, double width, double height) {
        return new BoundingBox(
            centerX - width / 2,
            centerY - height / 2,
            width,
            height
        );
    }
    
    public BoundingBox expand(double padding) {
        return new BoundingBox(
            x - padding,
            y - padding,
            width + 2 * padding,
            height + 2 * padding
        );
    }
}

