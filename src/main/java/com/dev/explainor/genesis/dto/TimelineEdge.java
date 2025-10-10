package com.dev.explainor.genesis.dto;

import com.dev.explainor.genesis.domain.Point;

import java.util.List;

public record TimelineEdge(
    String id,
    String from,
    String to,
    String label,
    List<Point> path,
    EdgeStyle edgeStyle,
    double pathLength
) {}

