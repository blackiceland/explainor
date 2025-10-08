package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.layout.model.LayoutConstraints;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.LayoutNode;
import com.dev.explainor.genesis.layout.model.PositionedNode;

import java.util.ArrayList;
import java.util.List;

public class DummyLayoutManager implements LayoutManager {

    private static final double NODE_SPACING = 250.0;

    @Override
    public List<PositionedNode> layout(
        List<LayoutNode> nodes, 
        List<LayoutEdge> edges, 
        LayoutConstraints constraints
    ) {
        return layoutNodesInLine(nodes, constraints);
    }

    private List<PositionedNode> layoutNodesInLine(List<LayoutNode> nodes, LayoutConstraints constraints) {
        List<PositionedNode> result = new ArrayList<>();
        if (nodes.isEmpty()) {
            return result;
        }
        
        double totalWidth = (nodes.size() - 1) * NODE_SPACING;
        double startX = -(totalWidth / 2.0);

        for (int i = 0; i < nodes.size(); i++) {
            LayoutNode node = nodes.get(i);
            double x = startX + (i * NODE_SPACING);
            
            result.add(new PositionedNode(
                node.id(),
                node.label(),
                node.icon(),
                x,
                0.0
            ));
        }
        
        return result;
    }
}

