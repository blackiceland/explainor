package com.dev.explainor.genesis.layout.model;

import com.dev.explainor.genesis.domain.Point;

import java.util.List;

public record RoutedEdge(
    String id,
    String from,
    String to,
    String label,
    List<Point> path,
    Point startAnchor,
    Point endAnchor
) {
    public Point effectiveStart() {
        return startAnchor != null ? startAnchor : (path.isEmpty() ? new Point(0, 0) : path.get(0));
    }
    
    public Point effectiveEnd() {
        return endAnchor != null ? endAnchor : (path.isEmpty() ? new Point(0, 0) : path.get(path.size() - 1));
    }
}

