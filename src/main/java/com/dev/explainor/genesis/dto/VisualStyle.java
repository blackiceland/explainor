package com.dev.explainor.genesis.dto;

public record VisualStyle(
    double width,
    double height,
    String shape,
    String backgroundColor,
    String borderColor,
    double borderWidth,
    double borderRadius,
    Shadow shadow,
    TextStyle textStyle
) {
    public static VisualStyle defaultNodeStyle() {
        return new VisualStyle(
            120.0,
            80.0,
            "rectangle",
            "#FFFFFF",
            "#E0E0E0",
            1.0,
            16.0,
            Shadow.defaultNodeShadow(),
            TextStyle.defaultNodeText()
        );
    }

    public record Shadow(
        double offsetX,
        double offsetY,
        double blurRadius,
        double spreadRadius,
        String color
    ) {
        public static Shadow defaultNodeShadow() {
            return new Shadow(0, 4, 8, 0, "rgba(0, 0, 0, 0.1)");
        }

        public static Shadow secondaryShadow() {
            return new Shadow(0, 10, 20, 0, "rgba(0, 0, 0, 0.15)");
        }
    }

    public record TextStyle(
        String fontFamily,
        double fontSize,
        int fontWeight,
        String color
    ) {
        public static TextStyle defaultNodeText() {
            return new TextStyle(
                "-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
                14.0,
                600,
                "#1F2937"
            );
        }

        public static TextStyle defaultEdgeText() {
            return new TextStyle(
                "-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
                12.0,
                500,
                "#374151"
            );
        }
    }
}

