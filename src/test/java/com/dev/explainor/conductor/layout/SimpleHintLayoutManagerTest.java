package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.service.SceneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHintLayoutManagerTest {

    private SimpleHintLayoutManager layoutManager;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        layoutManager = new SimpleHintLayoutManager();
        sceneState = new SceneState(1280, 720);
    }

    @Test
    void shouldPositionEntityOnLeft() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, "left")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.25, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldPositionEntityOnRight() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, "right")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.75, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldPositionEntityInCenter() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, "center")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldUseDefaultPositionForUnknownHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, "unknown")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldUseDefaultPositionForNullHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null)
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }
}

