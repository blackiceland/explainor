package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.layout.model.LayoutConstraints;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathFindingCoordinator implements PathFinder {

    private final GridBuilder gridBuilder;
    private final AStarPathSolver pathSolver;

    public PathFindingCoordinator(GridBuilder gridBuilder, AStarPathSolver pathSolver) {
        this.gridBuilder = gridBuilder;
        this.pathSolver = pathSolver;
    }

    @Override
    public List<RoutedEdge> routeEdges(List<LayoutEdge> edges, List<PositionedNode> nodes, LayoutConstraints constraints) {
        Objects.requireNonNull(edges);
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(constraints);
        Map<String, PositionedNode> index = nodes.stream()
            .collect(Collectors.toMap(PositionedNode::id, Function.identity()));
        List<RoutedEdge> routedEdges = new ArrayList<>();

        GridBuilder.GridSpecification grid = gridBuilder.build(nodes);

        for (LayoutEdge edge : edges) {
            PositionedNode fromNode = requireNode(index, edge.from());
            PositionedNode toNode = requireNode(index, edge.to());
            routedEdges.add(routeEdge(edge, fromNode, toNode, grid));
        }
        return routedEdges;
    }

    private RoutedEdge routeEdge(LayoutEdge edge, PositionedNode fromNode, PositionedNode toNode, GridBuilder.GridSpecification grid) {
        GridPoint start = gridBuilder.toGrid(fromNode.x(), fromNode.y());
        GridPoint goal = gridBuilder.toGrid(toNode.x(), toNode.y());
        List<GridPoint> gridPath = pathSolver.findPath(start, goal, grid.blockedCells());
        List<Point> routedPoints = new ArrayList<>();
        for (GridPoint gridPoint : gridPath) {
            routedPoints.add(new Point(gridPoint.x() * grid.step(), gridPoint.y() * grid.step()));
        }

        Point startAnchor = AnchorCalculator.calculateExitPoint(
            fromNode,
            routedPoints.isEmpty() ? new Point(toNode.x(), toNode.y()) : routedPoints.get(0)
        );
        Point endAnchor = AnchorCalculator.calculateEntryPoint(
            toNode,
            routedPoints.isEmpty() ? new Point(fromNode.x(), fromNode.y()) : routedPoints.get(routedPoints.size() - 1)
        );

        List<Point> fullPath = new ArrayList<>();
        fullPath.add(startAnchor);
        fullPath.addAll(routedPoints);
        fullPath.add(endAnchor);
        double pathLength = calculatePathLength(fullPath);

        return new RoutedEdge(edge.id(), edge.from(), edge.to(), edge.label(), edge.lineStyle(), routedPoints, startAnchor, endAnchor, pathLength, edge.params());
    }

    private double calculatePathLength(List<Point> path) {
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += distance(path.get(i), path.get(i + 1));
        }
        return length;
    }

    private double distance(Point first, Point second) {
        return Math.hypot(second.x() - first.x(), second.y() - first.y());
    }

    private PositionedNode requireNode(Map<String, PositionedNode> index, String id) {
        PositionedNode node = index.get(id);
        if (node == null) {
            throw new IllegalStateException("Node not found: " + id);
        }
        return node;
    }
}

