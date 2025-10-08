package com.dev.explainor.helios.service;

import com.dev.explainor.helios.domain.SceneEntity;
import lombok.Data;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class SceneState {
    private double currentTime = 0.0;
    private final Map<String, SceneEntity> entities = new HashMap<>();
    private final double canvasWidth;
    private final double canvasHeight;
    private final Graph<String, DefaultEdge> connectionsGraph;

    public SceneState(double canvasWidth, double canvasHeight, Graph<String, DefaultEdge> connectionsGraph) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.connectionsGraph = connectionsGraph;
    }

    public void advanceTime(double seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Cannot advance time by negative value: " + seconds);
        }
        this.currentTime += seconds;
    }

    public void registerEntity(SceneEntity entity) {
        entities.put(entity.id(), entity);
    }

    public Optional<SceneEntity> getEntityById(String id) {
        return Optional.ofNullable(entities.get(id));
    }

    public Map<String, SceneEntity> getEntities() {
        return Collections.unmodifiableMap(entities);
    }
}
