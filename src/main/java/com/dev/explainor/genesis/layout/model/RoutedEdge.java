package com.dev.explainor.genesis.layout.model;

import com.dev.explainor.genesis.domain.Point;

import java.util.List;

public record RoutedEdge(
    String id,
    String from,
    String to,
    String label,
    List<Point> path
) {}

