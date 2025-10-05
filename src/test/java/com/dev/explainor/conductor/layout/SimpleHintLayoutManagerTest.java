package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.ConnectEntitiesParams;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHintLayoutManagerTest {

    private SimpleHintLayoutManager layoutManager;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        layoutManager = new SimpleHintLayoutManager();
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        sceneState = new SceneState(1280, 720, graph);
    }

    @Test
    void shouldPositionEntityOnLeft() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "left")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.15, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldPositionEntityOnRight() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "right")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.85, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldPositionEntityInCenter() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "center")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldPositionEntityOnTop() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "top")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.75, position.x());
        assertEquals(720 * 0.15, position.y());
    }

    @Test
    void shouldPositionEntityOnBottom() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "bottom")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.75, position.x());
        assertEquals(720 * 0.85, position.y());
    }

    @Test
    void shouldPositionEntityOnTopLeft() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "top-left")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.15, position.x());
        assertEquals(720 * 0.15, position.y());
    }

    @Test
    void shouldPositionEntityOnTopRight() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "top-right")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.85, position.x());
        assertEquals(720 * 0.15, position.y());
    }

    @Test
    void shouldPositionEntityOnBottomLeft() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "bottom-left")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.15, position.x());
        assertEquals(720 * 0.85, position.y());
    }

    @Test
    void shouldPositionEntityOnBottomRight() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "bottom-right")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.85, position.x());
        assertEquals(720 * 0.85, position.y());
    }

    @Test
    void shouldUseDefaultPositionForUnknownHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "unknown")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldUseDefaultPositionForNullHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, null)
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldUseDefaultPositionForEmptyHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldHandleLowerCaseHints() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "left")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.15, position.x());
    }

    @Test
    void shouldNotHandleUpperCaseHints() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "LEFT")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
    }

    @Test
    void shouldReturnCenterForNonCreateEntityCommand() {
        Command command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("from", "to", "label")
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertEquals(1280 * 0.5, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldScalePositionForDifferentCanvasWidth() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        SceneState wideCanvas = new SceneState(1920, 720, graph);
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "left")
        );

        Point position = layoutManager.calculatePosition(command, wideCanvas);

        assertEquals(1920 * 0.15, position.x());
        assertEquals(720 * 0.5, position.y());
    }

    @Test
    void shouldScalePositionForDifferentCanvasHeight() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        SceneState tallCanvas = new SceneState(1280, 1080, graph);
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Label", null, null, "top")
        );

        Point position = layoutManager.calculatePosition(command, tallCanvas);

        assertEquals(1280 * 0.75, position.x());
        assertEquals(1080 * 0.15, position.y());
    }

    @Test
    void shouldReturnSamePositionForSameHint() {
        CreateEntityCommand command1 = new CreateEntityCommand(
            "entity1",
            new CreateEntityParams("Label 1", null, null, "left")
        );
        CreateEntityCommand command2 = new CreateEntityCommand(
            "entity2",
            new CreateEntityParams("Label 2", null, null, "left")
        );

        Point position1 = layoutManager.calculatePosition(command1, sceneState);
        Point position2 = layoutManager.calculatePosition(command2, sceneState);

        assertEquals(position1.x(), position2.x());
        assertEquals(position1.y(), position2.y());
    }

    @Test
    void shouldReturnDifferentPositionsForDifferentHints() {
        CreateEntityCommand leftCommand = new CreateEntityCommand(
            "entity1",
            new CreateEntityParams("Label", null, null, "left")
        );
        CreateEntityCommand rightCommand = new CreateEntityCommand(
            "entity2",
            new CreateEntityParams("Label", null, null, "right")
        );

        Point leftPosition = layoutManager.calculatePosition(leftCommand, sceneState);
        Point rightPosition = layoutManager.calculatePosition(rightCommand, sceneState);

        assertNotEquals(leftPosition.x(), rightPosition.x());
    }
}
