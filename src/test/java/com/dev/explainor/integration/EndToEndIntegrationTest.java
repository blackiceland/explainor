package com.dev.explainor.integration;

import com.dev.explainor.conductor.domain.*;
import com.dev.explainor.conductor.factory.*;
import com.dev.explainor.conductor.layout.SimpleHintLayoutManager;
import com.dev.explainor.conductor.service.ConductorService;
import com.dev.explainor.conductor.validation.StoryboardValidator;
import com.dev.explainor.renderer.domain.FinalTimeline;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests that simulate the full pipeline:
 * Storyboard → Conductor → FinalTimeline
 */
class EndToEndIntegrationTest {

    private ConductorService conductorService;

    @BeforeEach
    void setUp() {
        var layoutManager = new SimpleHintLayoutManager();
        var validator = new StoryboardValidator();
        var factories = List.of(
            new CreateEntityFactory(layoutManager),
            new ConnectEntitiesFactory(),
            new PauseFactory(),
            new FocusOnFactory()
        );
        conductorService = new ConductorService(factories, validator);
    }

    @Test
    void shouldGenerateHTTPRequestExplainerVideo() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer-desktop", null, "left")),
            new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, "right")),
            new PauseCommand("pause1", new PauseParams(1.0)),
            new ConnectEntitiesCommand("request", new ConnectEntitiesParams("client", "server", "HTTP Request")),
            new PauseCommand("pause2", new PauseParams(2.0)),
            new ConnectEntitiesCommand("response", new ConnectEntitiesParams("server", "client", "HTTP Response"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertTrue(timeline.totalDuration() >= 7.0);
        
        long shapeCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("shape"))
            .count();
        assertEquals(2, shapeCount);

        long arrowCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("arrow"))
            .count();
        assertEquals(2, arrowCount);

        long textCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("text"))
            .count();
        assertEquals(2, textCount);
    }

    @Test
    void shouldGenerateMicroservicesArchitectureVideo() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer-desktop", null, "left")),
            new CreateEntityCommand("api_gateway", new CreateEntityParams("API Gateway", "arrows-right-left", null, "center")),
            new PauseCommand("initial_pause", new PauseParams(1.0)),
            new ConnectEntitiesCommand("client_request", new ConnectEntitiesParams("client", "api_gateway", "Request")),
            new PauseCommand("pause_after_request", new PauseParams(0.5)),
            new CreateEntityCommand("microservice_1", new CreateEntityParams("Microservice 1", "server", null, "top-right")),
            new CreateEntityCommand("microservice_2", new CreateEntityParams("Microservice 2", "server", null, "right")),
            new CreateEntityCommand("microservice_3", new CreateEntityParams("Microservice 3", "server", null, "bottom-right")),
            new PauseCommand("pause_before_routing", new PauseParams(1.0)),
            new ConnectEntitiesCommand("route_to_service1", new ConnectEntitiesParams("api_gateway", "microservice_1", "Route")),
            new PauseCommand("pause_between_routes", new PauseParams(1.0)),
            new ConnectEntitiesCommand("route_to_service2", new ConnectEntitiesParams("api_gateway", "microservice_2", "Route")),
            new PauseCommand("pause_between_routes2", new PauseParams(1.0)),
            new ConnectEntitiesCommand("route_to_service3", new ConnectEntitiesParams("api_gateway", "microservice_3", "Route"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        
        long shapeCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("shape"))
            .count();
        assertEquals(5, shapeCount);

        long arrowCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("arrow"))
            .count();
        assertEquals(4, arrowCount);

        assertTrue(timeline.totalDuration() >= 11.0);
    }

    @Test
    void shouldGenerateDatabaseReplicationVideo() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("primary_db", new CreateEntityParams("Primary DB", "circle-stack", "circle", "left")),
            new CreateEntityCommand("replica_db", new CreateEntityParams("Replica DB", "circle-stack", "circle", "right")),
            new PauseCommand("pause1", new PauseParams(1.0)),
            new ConnectEntitiesCommand("replication", new ConnectEntitiesParams("primary_db", "replica_db", "Replication")),
            new FocusOnCommand("focus", new FocusOnParams(null, "full_scene", null))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);

        TimelineEvent primaryDb = timeline.timeline().stream()
            .filter(e -> e.elementId().equals("primary_db_shape"))
            .findFirst()
            .orElseThrow();
        assertEquals("circle", primaryDb.props().get("shapeType"));

        assertNotNull(timeline.camera());
        assertEquals(1, timeline.camera().size());
    }

    @Test
    void shouldFailWhenConnectingBeforeCreating() {
        Storyboard storyboard = new Storyboard(List.of(
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("entity1", "entity2", "Connection")),
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        assertThrows(IllegalArgumentException.class,
            () -> conductorService.generateTimeline(storyboard));
    }

    @Test
    void shouldFailWhenFocusingOnNonExistentEntity() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new FocusOnCommand("focus", new FocusOnParams("nonexistent", null, null))
        ));

        assertThrows(IllegalArgumentException.class,
            () -> conductorService.generateTimeline(storyboard));
    }

    @Test
    void shouldHandleLargeStoryboardEfficiently() {
        List<Command> commands = new java.util.ArrayList<>();
        
        for (int i = 0; i < 50; i++) {
            commands.add(new CreateEntityCommand(
                "entity" + i,
                new CreateEntityParams("Entity " + i, "server", null, "center")
            ));
        }

        for (int i = 0; i < 49; i++) {
            commands.add(new ConnectEntitiesCommand(
                "connection" + i,
                new ConnectEntitiesParams("entity" + i, "entity" + (i + 1), "Connection")
            ));
        }

        Storyboard storyboard = new Storyboard(commands);

        long startTime = System.currentTimeMillis();
        FinalTimeline timeline = conductorService.generateTimeline(storyboard);
        long endTime = System.currentTimeMillis();

        assertNotNull(timeline);
        assertTrue(timeline.timeline().size() > 0);
        
        assertTrue((endTime - startTime) < 5000,
            "Large storyboard processing took too long: " + (endTime - startTime) + "ms");
    }

    @Test
    void shouldPreserveAllDataThroughPipeline() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("custom_entity", 
                new CreateEntityParams("Custom Label", "custom-icon", "circle", "top-left"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        TimelineEvent shapeEvent = timeline.timeline().get(0);
        assertEquals("custom_entity_shape", shapeEvent.elementId());
        assertEquals("Custom Label", shapeEvent.props().get("label"));
        assertEquals("custom-icon", shapeEvent.props().get("icon"));
        assertEquals("circle", shapeEvent.props().get("shapeType"));
    }

    @Test
    void shouldMaintainTemporalCoherence() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new PauseCommand("pause1", new PauseParams(2.0)),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right")),
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("entity1", "entity2", "Connection"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(0.0, timeline.timeline().get(0).time());
        
        assertEquals(3.0, timeline.timeline().get(1).time());
        
        TimelineEvent arrow = timeline.timeline().stream()
            .filter(e -> e.type().equals("arrow"))
            .findFirst()
            .orElseThrow();
        assertEquals(4.0, arrow.time());
    }
}

