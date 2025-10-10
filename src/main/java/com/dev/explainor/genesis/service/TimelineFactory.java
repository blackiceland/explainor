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
            .map(routedEdge -> {
                EdgeStyle baseStyle = EdgeStyle.defaultEdgeStyle();
                String lineStyle = routedEdge.lineStyle() != null ? routedEdge.lineStyle() : baseStyle.lineStyle();
                
                EdgeStyle customStyle = new EdgeStyle(
                    baseStyle.strokeColor(),
                    baseStyle.strokeWidth(),
                    lineStyle,
                    baseStyle.arrowStyle(),
                    baseStyle.shadow(),
                    baseStyle.labelStyle()
                );
                
                return new TimelineEdge(
                    routedEdge.id(),
                    routedEdge.from(),
                    routedEdge.to(),
                    routedEdge.label(),
                    routedEdge.path(),
                    customStyle,
                    routedEdge.pathLength()
                );
            })
            .toList();
    }
}
