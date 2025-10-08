package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.layout.model.LayoutConstraints;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;

import java.util.List;

public interface PathFinder {
    List<RoutedEdge> routeEdges(List<LayoutEdge> edges, List<PositionedNode> nodes, LayoutConstraints constraints);
}



