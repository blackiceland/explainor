package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(since = "Genesis", forRemoval = true)
@Component
public class LinearChainLayoutStrategy implements LayoutStrategy {

    private static final double HORIZONTAL_SPACING = 350.0;
    private static final double MARGIN = 100.0;

    @Override
    public Map<String, Point> calculatePositions(Graph<String, DefaultEdge> graph, SceneState sceneState) {
        Map<String, Point> positions = new HashMap<>();

        List<String> orderedNodes = new ArrayList<>();
        
        try {
            TopologicalOrderIterator<String, DefaultEdge> iterator = new TopologicalOrderIterator<>(graph);
            iterator.forEachRemaining(orderedNodes::add);
        } catch (IllegalArgumentException e) {
            orderedNodes.addAll(graph.vertexSet());
        }

        if (orderedNodes.isEmpty()) {
            return positions;
        }

        double canvasWidth = sceneState.getCanvasWidth();
        double canvasHeight = sceneState.getCanvasHeight();
        double centerY = canvasHeight / 2.0;

        double totalWidth = orderedNodes.size() * HORIZONTAL_SPACING;
        double startX = (canvasWidth - totalWidth) / 2.0 + MARGIN;

        for (int i = 0; i < orderedNodes.size(); i++) {
            String nodeId = orderedNodes.get(i);
            double x = startX + i * HORIZONTAL_SPACING;
            positions.put(nodeId, new Point(x, centerY));
        }

        return positions;
    }

    @Override
    public String getName() {
        return "Linear Chain";
    }
}

