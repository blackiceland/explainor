package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.EdgeStyle;
import com.dev.explainor.genesis.dto.TimelineEdge;
import com.dev.explainor.genesis.dto.TimelineNode;
import com.dev.explainor.genesis.dto.VisualStyle;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StylingService {

    public List<TimelineNode> toTimelineNodes(List<PositionedNode> positionedNodes) {
        return positionedNodes.stream()
            .map(this::toTimelineNode)
            .toList();
    }

    public List<TimelineEdge> toTimelineEdges(List<RoutedEdge> routedEdges) {
        return routedEdges.stream()
            .map(this::toTimelineEdge)
            .toList();
    }

    private TimelineNode toTimelineNode(PositionedNode node) {
        return new TimelineNode(
            node.id(),
            node.label(),
            node.icon(),
            node.x(),
            node.y(),
            VisualStyle.defaultNodeStyle()
        );
    }

    private TimelineEdge toTimelineEdge(RoutedEdge edge) {
        EdgeStyle baseStyle = EdgeStyle.defaultEdgeStyle();
        String finalLineStyle = resolveLineStyle(edge);
        String arrowStyle = resolveArrowStyle(edge, baseStyle);
        EdgeStyle customStyle = new EdgeStyle(
            baseStyle.strokeColor(),
            baseStyle.strokeWidth(),
            finalLineStyle,
            arrowStyle,
            baseStyle.shadow(),
            baseStyle.labelStyle()
        );
        return new TimelineEdge(edge.id(), edge.from(), edge.to(), edge.label(), edge.path(), customStyle, edge.pathLength());
    }

    private String resolveLineStyle(RoutedEdge edge) {
        if (edge.params() != null) {
            if (edge.params().isDashed()) {
                return "dashed";
            }
            return edge.params().lineStyleOrDefault(defaultLineStyle());
        }
        return edge.lineStyle() != null && !edge.lineStyle().isBlank() ? edge.lineStyle() : defaultLineStyle();
    }

    private String resolveArrowStyle(RoutedEdge edge, EdgeStyle baseStyle) {
        if (edge.params() != null) {
            return edge.params().arrowStyleOrDefault(baseStyle.arrowStyle());
        }
        return baseStyle.arrowStyle();
    }

    private String defaultLineStyle() {
        return EdgeStyle.defaultEdgeStyle().lineStyle();
    }
}

