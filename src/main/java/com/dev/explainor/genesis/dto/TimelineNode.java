package com.dev.explainor.genesis.dto;

public record TimelineNode(
    String id,
    String label,
    String icon,
    double x,
    double y,
    VisualStyle visualStyle
) {}

