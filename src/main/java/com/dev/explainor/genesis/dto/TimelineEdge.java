package com.dev.explainor.genesis.dto;

import java.util.List;

public record TimelineEdge(
    String id,
    String from,
    String to,
    String label,
    List<Point> path
) {}

