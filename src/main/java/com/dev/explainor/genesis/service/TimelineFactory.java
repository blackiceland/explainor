package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.EdgeStyle;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.Stage;
import com.dev.explainor.genesis.dto.TimelineEdge;
import com.dev.explainor.genesis.dto.TimelineNode;
import com.dev.explainor.genesis.dto.VisualStyle;
import com.dev.explainor.genesis.layout.model.LayoutResult;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimelineFactory {

    private static final int DEFAULT_CANVAS_WIDTH = 1280;
    private static final int DEFAULT_CANVAS_HEIGHT = 720;

    public FinalTimelineV1 createFrom(LayoutResult layoutResult, List<AnimationTrack> animationTracks) {
        Stage stage = new Stage(
            DEFAULT_CANVAS_WIDTH,
            DEFAULT_CANVAS_HEIGHT
        );

        List<TimelineNode> nodes = convertToNodes(layoutResult.nodes());
        List<TimelineEdge> edges = convertToEdges(layoutResult.edges());

        return FinalTimelineV1.create(stage, nodes, edges, animationTracks);
    }

    private List<TimelineNode> convertToNodes(List<PositionedNode> positionedNodes) {
        return positionedNodes.stream()
            .map(positionedNode -> new TimelineNode(
                positionedNode.id(),
                positionedNode.label(),
                positionedNode.icon(),
                positionedNode.x(),
                positionedNode.y(),
                VisualStyle.defaultNodeStyle()
            ))
            .toList();
    }

    private List<TimelineEdge> convertToEdges(List<RoutedEdge> routedEdges) {
        return routedEdges.stream()
            .map(routedEdge -> new TimelineEdge(
                routedEdge.id(),
                routedEdge.from(),
                routedEdge.to(),
                routedEdge.label(),
                routedEdge.path(),
                EdgeStyle.defaultEdgeStyle(),
                calculatePathLength(routedEdge.path())
            ))
            .toList();
    }
    
    private double calculatePathLength(List<com.dev.explainor.genesis.domain.Point> path) {
        if (path.isEmpty()) {
            return 0.0;
        }
        
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            com.dev.explainor.genesis.domain.Point p1 = path.get(i);
            com.dev.explainor.genesis.domain.Point p2 = path.get(i + 1);
            double dx = p2.x() - p1.x();
            double dy = p2.y() - p1.y();
            length += Math.sqrt(dx * dx + dy * dy);
        }
        return length;
    }
}
