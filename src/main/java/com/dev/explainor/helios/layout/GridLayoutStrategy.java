package com.dev.explainor.helios.layout;

import com.dev.explainor.helios.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(since = "Genesis", forRemoval = true)
@Component
public class GridLayoutStrategy implements LayoutStrategy {

    private static final double ENTITY_WIDTH = 200.0;
    private static final double ENTITY_HEIGHT = 80.0;
    private static final double SPACING_X = 300.0;
    private static final double SPACING_Y = 150.0;

    @Override
    public Map<String, Point> calculatePositions(Graph<String, DefaultEdge> graph, SceneState sceneState) {
        Map<String, Point> positions = new HashMap<>();
        List<String> entityIds = graph.vertexSet().stream().toList();

        if (entityIds.isEmpty()) {
            return positions;
        }

        double canvasWidth = sceneState.getCanvasWidth();
        double canvasHeight = sceneState.getCanvasHeight();

        int entitiesCount = entityIds.size();
        int columns = (int) Math.ceil(Math.sqrt(entitiesCount));
        int rows = (int) Math.ceil((double) entitiesCount / columns);

        double totalWidth = columns * ENTITY_WIDTH + (columns - 1) * SPACING_X;
        double totalHeight = rows * ENTITY_HEIGHT + (rows - 1) * SPACING_Y;

        double startX = (canvasWidth - totalWidth) / 2.0;
        double startY = (canvasHeight - totalHeight) / 2.0;

        for (int i = 0; i < entityIds.size(); i++) {
            String entityId = entityIds.get(i);
            int row = i / columns;
            int col = i % columns;

            double x = startX + col * (ENTITY_WIDTH + SPACING_X);
            double y = startY + row * (ENTITY_HEIGHT + SPACING_Y);

            positions.put(entityId, new Point(x, y));
        }

        return positions;
    }

    @Override
    public String getName() {
        return "Grid";
    }
}

