package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HierarchicalLayoutStrategy implements LayoutStrategy {

    private static final double LAYER_SPACING_X = 350.0;
    private static final double NODE_SPACING_Y = 150.0;
    private static final double MARGIN = 100.0;

    @Override
    public Map<String, Point> calculatePositions(Graph<String, DefaultEdge> graph, SceneState sceneState) {
        Map<String, Point> positions = new HashMap<>();

        Map<String, Integer> layers = assignLayers(graph);
        Map<Integer, List<String>> nodesByLayer = groupNodesByLayer(layers);

        double canvasWidth = sceneState.getCanvasWidth();
        double canvasHeight = sceneState.getCanvasHeight();

        int maxLayer = nodesByLayer.keySet().stream().max(Integer::compare).orElse(0);
        double availableWidth = canvasWidth - 2 * MARGIN;
        double layerSpacing = maxLayer > 0 
            ? Math.min(LAYER_SPACING_X, availableWidth / maxLayer)
            : LAYER_SPACING_X;

        for (Map.Entry<Integer, List<String>> entry : nodesByLayer.entrySet()) {
            Integer layerIndex = entry.getKey();
            List<String> nodesInLayer = entry.getValue();

            double x = MARGIN + layerIndex * layerSpacing;

            double totalLayerHeight = nodesInLayer.size() * NODE_SPACING_Y;
            double startY = (canvasHeight - totalLayerHeight) / 2.0;

            for (int i = 0; i < nodesInLayer.size(); i++) {
                String nodeId = nodesInLayer.get(i);
                double y = startY + i * NODE_SPACING_Y + NODE_SPACING_Y / 2.0;
                positions.put(nodeId, new Point(x, y));
            }
        }

        return positions;
    }

    private Map<String, Integer> assignLayers(Graph<String, DefaultEdge> graph) {
        Map<String, Integer> layers = new HashMap<>();

        List<String> rootNodes = graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) == 0)
                .toList();

        if (rootNodes.isEmpty() && !graph.vertexSet().isEmpty()) {
            rootNodes = List.of(graph.vertexSet().iterator().next());
        }

        Queue<String> queue = new LinkedList<>(rootNodes);
        for (String root : rootNodes) {
            layers.put(root, 0);
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentLayer = layers.get(current);

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                String target = graph.getEdgeTarget(edge);
                if (!layers.containsKey(target)) {
                    layers.put(target, currentLayer + 1);
                    queue.add(target);
                } else {
                    layers.put(target, Math.max(layers.get(target), currentLayer + 1));
                }
            }
        }

        for (String vertex : graph.vertexSet()) {
            if (!layers.containsKey(vertex)) {
                layers.put(vertex, rootNodes.isEmpty() ? 0 : layers.values().stream().max(Integer::compare).orElse(0) + 1);
            }
        }

        return layers;
    }

    private Map<Integer, List<String>> groupNodesByLayer(Map<String, Integer> layers) {
        Map<Integer, List<String>> result = new HashMap<>();

        for (Map.Entry<String, Integer> entry : layers.entrySet()) {
            result.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        return result;
    }

    @Override
    public String getName() {
        return "Hierarchical";
    }
}

