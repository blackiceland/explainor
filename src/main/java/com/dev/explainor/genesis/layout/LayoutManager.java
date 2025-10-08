package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.layout.model.*;

import java.util.List;

public interface LayoutManager {
    
    List<PositionedNode> layout(List<LayoutNode> nodes, List<LayoutEdge> edges, LayoutConstraints constraints);
}
