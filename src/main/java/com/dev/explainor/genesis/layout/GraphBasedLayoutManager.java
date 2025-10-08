package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.layout.model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphBasedLayoutManager implements LayoutManager {

    private final double layerSpacing;
    private final double nodeSpacing;

    public GraphBasedLayoutManager(LayoutProperties properties) {
        this.layerSpacing = properties.getLayerSpacing();
        this.nodeSpacing = properties.getNodeSpacing();
    }

    @Override
    public List<PositionedNode> layout(List<LayoutNode> nodes, List<LayoutEdge> edges, LayoutConstraints constraints) {
        if (nodes.isEmpty()) {
            return List.of();
        }

        Graph<String, DefaultEdge> graph = buildGraph(nodes, edges);
        Map<String, Integer> levels = assignLevels(graph);
        Map<String, Integer> orders = assignOrdersPerLevel(nodes, levels);

        List<PositionedNode> positionedNodes = calculatePositions(nodes, levels, orders);
        centerAndScale(positionedNodes, constraints);

        return positionedNodes;
    }

    private Graph<String, DefaultEdge> buildGraph(List<LayoutNode> nodes, List<LayoutEdge> edges) {
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for (LayoutNode node : nodes) {
            graph.addVertex(node.id());
        }
        for (LayoutEdge edge : edges) {
            graph.addEdge(edge.from(), edge.to());
        }
        return graph;
    }

    private Map<String, Integer> assignLevels(Graph<String, DefaultEdge> graph) {
        Map<String, Integer> levels = new HashMap<>();
        Set<String> visited = new HashSet<>();

        List<String> roots = new ArrayList<>();
        for (String v : graph.vertexSet()) {
            if (graph.inDegreeOf(v) == 0) {
                roots.add(v);
            }
        }
        if (roots.isEmpty() && !graph.vertexSet().isEmpty()) {
            roots.add(graph.vertexSet().iterator().next());
        }

        ArrayDeque<String> queue = new ArrayDeque<>();
        for (String root : roots) {
            levels.put(root, 0);
            queue.add(root);
            visited.add(root);
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int nextLevel = levels.get(current) + 1;
            for (DefaultEdge edge : graph.edgesOf(current)) {
                String neighbor = getOppositeVertex(graph, edge, current);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    levels.put(neighbor, nextLevel);
                    queue.add(neighbor);
                }
            }
        }
        return levels;
    }

    private String getOppositeVertex(Graph<String, DefaultEdge> graph, DefaultEdge edge, String vertex) {
        return graph.getEdgeSource(edge).equals(vertex) ? graph.getEdgeTarget(edge) : graph.getEdgeSource(edge);
    }

    private Map<String, Integer> assignOrdersPerLevel(List<LayoutNode> nodes, Map<String, Integer> levels) {
        Map<Integer, Integer> counters = new HashMap<>();
        Map<String, Integer> order = new HashMap<>();
        for (LayoutNode n : nodes) {
            int lvl = levels.getOrDefault(n.id(), 0);
            int idx = counters.getOrDefault(lvl, 0);
            order.put(n.id(), idx);
            counters.put(lvl, idx + 1);
        }
        return order;
    }

    private List<PositionedNode> calculatePositions(
        List<LayoutNode> nodes,
        Map<String, Integer> levels,
        Map<String, Integer> orders
    ) {
        List<PositionedNode> positions = new ArrayList<>();
        Map<Integer, Integer> levelCounts = new HashMap<>();
        for (Integer level : orders.values()) {
            levelCounts.merge(level, 1, Integer::sum);
        }

        for (LayoutNode node : nodes) {
            int level = levels.getOrDefault(node.id(), 0);
            int order = orders.getOrDefault(node.id(), 0);
            int nodesInLevel = levelCounts.getOrDefault(level, 1);

            double x = (order - (nodesInLevel - 1) / 2.0) * nodeSpacing;
            double y = level * layerSpacing;

            positions.add(new PositionedNode(node.id(), node.label(), node.icon(), x, y));
        }
        return positions;
    }

    private void centerAndScale(List<PositionedNode> nodes, LayoutConstraints constraints) {
        if (nodes.isEmpty()) {
            return;
        }

        Bounds bounds = calculateBounds(nodes);
        double contentWidth = bounds.maxX() - bounds.minX();
        double contentHeight = bounds.maxY() - bounds.minY();

        double offsetX = -bounds.minX() - contentWidth / 2.0;
        double offsetY = -bounds.minY() - contentHeight / 2.0;

        nodes.replaceAll(node -> new PositionedNode(
            node.id(),
            node.label(),
            node.icon(),
            node.x() + offsetX,
            node.y() + offsetY
        ));
    }

    private Bounds calculateBounds(List<PositionedNode> nodes) {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (PositionedNode node : nodes) {
            minX = Math.min(minX, node.x());
            maxX = Math.max(maxX, node.x());
            minY = Math.min(minY, node.y());
            maxY = Math.max(maxY, node.y());
        }

        return new Bounds(minX, maxX, minY, maxY);
    }

    private record Bounds(double minX, double maxX, double minY, double maxY) {
    }
}


