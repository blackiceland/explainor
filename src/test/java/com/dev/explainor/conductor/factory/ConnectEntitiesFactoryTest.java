package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.ConnectEntitiesParams;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.service.SceneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectEntitiesFactoryTest {

    private ConnectEntitiesFactory factory;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        factory = new ConnectEntitiesFactory();
        org.jgrapht.Graph<String, org.jgrapht.graph.DefaultEdge> graph = new org.jgrapht.graph.SimpleDirectedGraph<>(org.jgrapht.graph.DefaultEdge.class);
        sceneState = new SceneState(1280, 720, graph);
    }

    @Test
    void shouldThrowExceptionWhenSourceEntityNotFound() {
        sceneState.registerEntity(new SceneEntity("server", 800, 360, 200, 120));

        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("nonexistent", "server", "Request")
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTimelineEvents(command, sceneState)
        );

        assertTrue(exception.getMessage().contains("source entity 'nonexistent' not found"));
    }

    @Test
    void shouldThrowExceptionWhenTargetEntityNotFound() {
        sceneState.registerEntity(new SceneEntity("client", 320, 360, 200, 120));

        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("client", "nonexistent", "Request")
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.createTimelineEvents(command, sceneState)
        );

        assertTrue(exception.getMessage().contains("target entity 'nonexistent' not found"));
    }

    @Test
    void shouldCreateEventsWhenBothEntitiesExist() {
        sceneState.registerEntity(new SceneEntity("client", 320, 360, 200, 120));
        sceneState.registerEntity(new SceneEntity("server", 800, 360, 200, 120));

        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("client", "server", "HTTP Request")
        );

        var events = factory.createTimelineEvents(command, sceneState);

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("connection_arrow", events.get(0).elementId());
        assertEquals("connection_label", events.get(1).elementId());
    }

    @Test
    void shouldCreateOnlyArrowWhenLabelIsNull() {
        sceneState.registerEntity(new SceneEntity("client", 320, 360, 200, 120));
        sceneState.registerEntity(new SceneEntity("server", 800, 360, 200, 120));

        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("client", "server", null)
        );

        var events = factory.createTimelineEvents(command, sceneState);

        assertEquals(1, events.size());
        assertEquals("connection_arrow", events.get(0).elementId());
    }

    @Test
    void shouldCreateOnlyArrowWhenLabelIsBlank() {
        sceneState.registerEntity(new SceneEntity("client", 320, 360, 200, 120));
        sceneState.registerEntity(new SceneEntity("server", 800, 360, 200, 120));

        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("client", "server", "   ")
        );

        var events = factory.createTimelineEvents(command, sceneState);

        assertEquals(1, events.size());
    }
}

