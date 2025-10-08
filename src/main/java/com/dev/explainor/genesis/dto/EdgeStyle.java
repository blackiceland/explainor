package com.dev.explainor.genesis.dto;

public record EdgeStyle(
    String strokeColor,
    double strokeWidth,
    String arrowStyle,
    VisualStyle.Shadow shadow,
    LabelStyle labelStyle
) {
    public static EdgeStyle defaultEdgeStyle() {
        return new EdgeStyle(
            "#374151",
            3.0,
            "triangle",
            new VisualStyle.Shadow(0, 2, 4, 0, "rgba(0, 0, 0, 0.15)"),
            LabelStyle.defaultLabelStyle()
        );
    }

    public record LabelStyle(
        String backgroundColor,
        double borderRadius,
        double paddingX,
        double paddingY,
        VisualStyle.Shadow shadow,
        VisualStyle.TextStyle textStyle
    ) {
        public static LabelStyle defaultLabelStyle() {
            return new LabelStyle(
                "#FFFFFF",
                6.0,
                8.0,
                4.0,
                new VisualStyle.Shadow(0, 2, 8, 0, "rgba(0, 0, 0, 0.1)"),
                VisualStyle.TextStyle.defaultEdgeText()
            );
        }
    }
}

