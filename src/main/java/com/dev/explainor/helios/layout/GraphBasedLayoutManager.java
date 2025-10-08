package com.dev.explainor.helios.layout;

import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Deprecated(since = "Genesis", forRemoval = true)
@Component
public class GraphBasedLayoutManager implements LayoutManager {

    private static final Logger log = LoggerFactory.getLogger(GraphBasedLayoutManager.class);
    
    private static final double ENTITY_WIDTH = 200.0;
    private static final double ENTITY_HEIGHT = 80.0;
    private static final double MARGIN_X = 100.0;
    private static final double MARGIN_Y = 100.0;

    private final PatternDetector patternDetector;

    private final Map<String, Point> cachedPositions = new HashMap<>();
    private boolean layoutCalculated = false;

    public GraphBasedLayoutManager(PatternDetector patternDetector) {
        this.patternDetector = patternDetector;
    }

    @Override
    public Point calculatePosition(Command command, SceneState sceneState) {
        if (!(command instanceof CreateEntityCommand createEntityCommand)) {
            return new Point(sceneState.getCanvasWidth() * 0.5, sceneState.getCanvasHeight() * 0.5);
        }

        if (!layoutCalculated) {
            calculateAllPositions(sceneState);
            layoutCalculated = true;
        }

        Point position = cachedPositions.get(createEntityCommand.id());
        if (position != null) {
            return position;
        }

        Point hintPosition = applyPositionHint(createEntityCommand, sceneState);
        if (hintPosition != null) {
            cachedPositions.put(createEntityCommand.id(), hintPosition);
            return hintPosition;
        }

        log.warn("No cached position found for entity: {}. Using center.", createEntityCommand.id());
        return new Point(sceneState.getCanvasWidth() * 0.5, sceneState.getCanvasHeight() * 0.5);
    }

    private void calculateAllPositions(SceneState sceneState) {
        cachedPositions.clear();

        Graph<String, DefaultEdge> graph = sceneState.getConnectionsGraph();
        
        if (graph.vertexSet().isEmpty()) {
            log.debug("Graph is empty, no positions to calculate");
            return;
        }

        LayoutStrategy strategy = patternDetector.detectPattern(graph);
        log.info("Using layout strategy: {}", strategy.getName());

        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);
        
        adjustForCanvasBounds(positions, sceneState.getCanvasWidth(), sceneState.getCanvasHeight());
        applyDynamicSpacing(positions, sceneState);
        
        cachedPositions.putAll(positions);

        log.debug("Calculated positions for {} entities", cachedPositions.size());
    }

    private Point applyPositionHint(CreateEntityCommand command, SceneState sceneState) {
        String hint = command.params().positionHint();
        if (hint == null || hint.isBlank()) {
            return null;
        }

        double width = sceneState.getCanvasWidth();
        double height = sceneState.getCanvasHeight();
        double margin = 0.15;

        return switch (hint.toLowerCase()) {
            case "left" -> new Point(width * margin, height * 0.5);
            case "right" -> new Point(width * (1 - margin), height * 0.5);
            case "top" -> new Point(width * 0.5, height * margin);
            case "bottom" -> new Point(width * 0.5, height * (1 - margin));
            case "center" -> new Point(width * 0.5, height * 0.5);
            case "top-left" -> new Point(width * margin, height * margin);
            case "top-right" -> new Point(width * (1 - margin), height * margin);
            case "bottom-left" -> new Point(width * margin, height * (1 - margin));
            case "bottom-right" -> new Point(width * (1 - margin), height * (1 - margin));
            default -> null;
        };
    }

    private void adjustForCanvasBounds(Map<String, Point> positions, double canvasWidth, double canvasHeight) {
        if (positions.isEmpty()) {
            return;
        }

        double minX = positions.values().stream().mapToDouble(Point::x).min().orElse(0);
        double maxX = positions.values().stream().mapToDouble(p -> p.x() + ENTITY_WIDTH).max().orElse(canvasWidth);
        double minY = positions.values().stream().mapToDouble(Point::y).min().orElse(0);
        double maxY = positions.values().stream().mapToDouble(p -> p.y() + ENTITY_HEIGHT).max().orElse(canvasHeight);

        double contentWidth = maxX - minX;
        double contentHeight = maxY - minY;
        
        double scaleX = (canvasWidth - 2 * MARGIN_X) / contentWidth;
        double scaleY = (canvasHeight - 2 * MARGIN_Y) / contentHeight;
        double scale = Math.min(Math.min(scaleX, scaleY), 1.0);

        double offsetX = (canvasWidth - contentWidth * scale) / 2.0 - minX * scale;
        double offsetY = (canvasHeight - contentHeight * scale) / 2.0 - minY * scale;

        positions.replaceAll((id, original) -> new Point(
                original.x() * scale + offsetX,
                original.y() * scale + offsetY
        ));
    }

    private void applyDynamicSpacing(Map<String, Point> positions, SceneState sceneState) {
        int entityCount = positions.size();
        if (entityCount <= 3) {
            return;
        }

        double densityFactor = Math.max(0.5, 1.0 - (entityCount - 3) * 0.05);
        
        double centerX = sceneState.getCanvasWidth() / 2.0;
        double centerY = sceneState.getCanvasHeight() / 2.0;

        positions.replaceAll((id, original) -> new Point(
                centerX + (original.x() - centerX) * densityFactor,
                centerY + (original.y() - centerY) * densityFactor
        ));
    }

    public void reset() {
        cachedPositions.clear();
        layoutCalculated = false;
    }
}
