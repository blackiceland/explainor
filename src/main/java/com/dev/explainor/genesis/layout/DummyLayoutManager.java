package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.dto.Point;
import com.dev.explainor.genesis.layout.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DummyLayoutManager implements LayoutManager {

    private static final double NODE_SPACING = 250.0;

    @Override
    public LayoutResult layout(
        List<LayoutNode> nodes, 
        List<LayoutEdge> edges, 
        LayoutConstraints constraints
    ) {
        List<PositionedNode> positionedNodes = layoutNodesInLine(nodes, constraints);
        List<RoutedEdge> routedEdges = routeEdgesStraight(edges, positionedNodes);
        
        return new LayoutResult(positionedNodes, routedEdges);
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

    private List<RoutedEdge> routeEdgesStraight(
        List<LayoutEdge> edges, 
        List<PositionedNode> nodes
    ) {
        List<RoutedEdge> result = new ArrayList<>();
        
        for (LayoutEdge edge : edges) {
            PositionedNode from = findNode(nodes, edge.from());
            PositionedNode to = findNode(nodes, edge.to());
            
            if (from == null || to == null) {
                throw new IllegalStateException(
                    "Edge '%s' references non-existent node(s): from='%s' (exists=%s), to='%s' (exists=%s)"
                        .formatted(edge.id(), edge.from(), from != null, edge.to(), to != null)
                );
            }
            
            List<Point> path = List.of(
                new Point(from.x(), from.y()),
                new Point(to.x(), to.y())
            );
            
            result.add(new RoutedEdge(
                edge.id(),
                edge.from(),
                edge.to(),
                edge.label(),
                path
            ));
        }
        
        return result;
    }

    private PositionedNode findNode(List<PositionedNode> nodes, String id) {
        return nodes.stream()
            .filter(n -> n.id().equals(id))
            .findFirst()
            .orElse(null);
    }
}

