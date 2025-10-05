package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HubAndSpokeLayoutStrategy implements LayoutStrategy {

    private static final double MIN_RADIUS = 250.0;
    private static final double RADIUS_PER_SPOKE = 30.0;

    @Override
    public Map<String, Point> calculatePositions(Graph<String, DefaultEdge> graph, SceneState sceneState) {
        Map<String, Point> positions = new HashMap<>();

        double canvasWidth = sceneState.getCanvasWidth();
        double canvasHeight = sceneState.getCanvasHeight();
        double centerX = canvasWidth / 2.0;
        double centerY = canvasHeight / 2.0;

        String hubId = findHub(graph);
        if (hubId == null) {
            List<String> allNodes = graph.vertexSet().stream().toList();
            double radius = Math.max(MIN_RADIUS, MIN_RADIUS + allNodes.size() * RADIUS_PER_SPOKE);
            for (int i = 0; i < allNodes.size(); i++) {
                double angle = 2 * Math.PI * i / allNodes.size();
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                positions.put(allNodes.get(i), new Point(x, y));
            }
            return positions;
        }

        positions.put(hubId, new Point(centerX, centerY));

        List<String> spokes = graph.vertexSet().stream()
                .filter(v -> !v.equals(hubId))
                .toList();

        int spokesCount = spokes.size();
        double radius = Math.max(MIN_RADIUS, MIN_RADIUS + spokesCount * RADIUS_PER_SPOKE);
        
        for (int i = 0; i < spokesCount; i++) {
            double angle = 2 * Math.PI * i / spokesCount;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.put(spokes.get(i), new Point(x, y));
        }

        return positions;
    }

    private String findHub(Graph<String, DefaultEdge> graph) {
        return graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) >= 2 || graph.inDegreeOf(v) >= 2)
                .max((v1, v2) -> {
                    int degree1 = Math.max(graph.outDegreeOf(v1), graph.inDegreeOf(v1));
                    int degree2 = Math.max(graph.outDegreeOf(v2), graph.inDegreeOf(v2));
                    return Integer.compare(degree1, degree2);
                })
                .orElse(null);
    }

    @Override
    public String getName() {
        return "Hub and Spoke";
    }
}

