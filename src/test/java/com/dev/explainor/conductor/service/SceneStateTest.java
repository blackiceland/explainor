package com.dev.explainor.conductor.service;

import com.dev.explainor.conductor.domain.SceneEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SceneStateTest {

    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        sceneState = new SceneState(1280, 720);
    }

    @Test
    void shouldInitializeWithZeroTime() {
        assertEquals(0.0, sceneState.getCurrentTime());
    }

    @Test
    void shouldAdvanceTimeByPositiveValue() {
        sceneState.advanceTime(1.5);
        assertEquals(1.5, sceneState.getCurrentTime());

        sceneState.advanceTime(2.0);
        assertEquals(3.5, sceneState.getCurrentTime());
    }

    @Test
    void shouldThrowExceptionForNegativeTime() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sceneState.advanceTime(-1.0)
        );

        assertTrue(exception.getMessage().contains("Cannot advance time by negative value"));
    }

    @Test
    void shouldAllowZeroTimeAdvancement() {
        sceneState.advanceTime(0.0);
        assertEquals(0.0, sceneState.getCurrentTime());
    }

    @Test
    void shouldRegisterAndRetrieveEntity() {
        SceneEntity entity = new SceneEntity("client", 100, 200, 150, 100);
        sceneState.registerEntity(entity);

        var retrieved = sceneState.getEntityById("client");
        assertTrue(retrieved.isPresent());
        assertEquals("client", retrieved.get().id());
        assertEquals(100, retrieved.get().x());
        assertEquals(200, retrieved.get().y());
    }

    @Test
    void shouldReturnEmptyOptionalForNonexistentEntity() {
        var retrieved = sceneState.getEntityById("nonexistent");
        assertTrue(retrieved.isEmpty());
    }

    @Test
    void shouldReturnImmutableCopyOfEntities() {
        SceneEntity entity1 = new SceneEntity("client", 100, 200, 150, 100);
        SceneEntity entity2 = new SceneEntity("server", 500, 200, 150, 100);

        sceneState.registerEntity(entity1);
        sceneState.registerEntity(entity2);

        var entities = sceneState.getEntities();
        assertEquals(2, entities.size());

        assertThrows(UnsupportedOperationException.class, () -> {
            entities.put("new", new SceneEntity("new", 0, 0, 0, 0));
        });
    }

    @Test
    void shouldStoreCanvasDimensions() {
        assertEquals(1280, sceneState.getCanvasWidth());
        assertEquals(720, sceneState.getCanvasHeight());
    }
}

