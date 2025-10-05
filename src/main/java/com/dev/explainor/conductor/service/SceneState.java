package com.dev.explainor.conductor.service;

import com.dev.explainor.conductor.domain.SceneEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class SceneState {
    private double currentTime = 0.0;
    private final Map<String, SceneEntity> entities = new HashMap<>();
    private final double canvasWidth;
    private final double canvasHeight;

    public SceneState(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
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
        return Map.copyOf(entities);
    }
}
