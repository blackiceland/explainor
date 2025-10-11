package com.dev.explainor.genesis.layout.model;

import com.dev.explainor.genesis.domain.ConnectEntitiesParams;

public record LayoutEdge(
    String id,
    String from,
    String to,
    String label,
    String lineStyle,
    ConnectEntitiesParams params
) {}

