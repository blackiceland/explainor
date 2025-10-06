package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.layout.model.*;

import java.util.List;

public interface LayoutManager {
    
    LayoutResult layout(List<LayoutNode> nodes, List<LayoutEdge> edges, LayoutConstraints constraints);
}
