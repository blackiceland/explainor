package com.dev.explainor.genesis.layout.model;

import java.util.List;

public record LayoutResult(
    List<PositionedNode> nodes,
    List<RoutedEdge> edges
) {}

