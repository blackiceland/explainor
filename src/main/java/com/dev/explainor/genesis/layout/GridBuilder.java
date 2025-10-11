package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GridBuilder {

    private final double gridStep;

    public GridBuilder(LayoutProperties properties) {
        this.gridStep = properties.getGridStep();
    }

    public GridSpecification build(List<PositionedNode> nodes) {
        Set<GridPoint> blockedCells = collectBlockedCells(nodes);
        return new GridSpecification(gridStep, blockedCells);
    }

    public GridPoint toGrid(double x, double y) {
        long gridX = Math.round(x / gridStep);
        long gridY = Math.round(y / gridStep);
        return new GridPoint((int) gridX, (int) gridY);
    }

    private Set<GridPoint> collectBlockedCells(List<PositionedNode> nodes) {
        return nodes.stream()
            .map(node -> toGrid(node.x(), node.y()))
            .collect(Collectors.toSet());
    }

    public record GridSpecification(double step, Set<GridPoint> blockedCells) {
    }
}

