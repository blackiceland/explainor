package com.dev.explainor.conductor;

import com.dev.explainor.conductor.domain.*;
import com.dev.explainor.conductor.factory.ConnectEntitiesFactory;
import com.dev.explainor.conductor.factory.CreateEntityFactory;
import com.dev.explainor.conductor.factory.CommandFactory;
import com.dev.explainor.conductor.factory.PauseFactory;
import com.dev.explainor.conductor.factory.FocusOnFactory;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.SimpleHintLayoutManager;
import com.dev.explainor.conductor.service.ConductorService;
import com.dev.explainor.conductor.validation.StoryboardValidator;
import com.dev.explainor.renderer.domain.FinalTimeline;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConductorServiceTest {

    private ConductorService conductorService;

    @BeforeEach
    void setUp() {
        LayoutManager layoutManager = new SimpleHintLayoutManager();
        StoryboardValidator validator = new StoryboardValidator();
        List<CommandFactory> factories = List.of(
            new CreateEntityFactory(layoutManager),
            new ConnectEntitiesFactory(),
            new PauseFactory(),
            new FocusOnFactory()
        );
        conductorService = new ConductorService(factories, validator);
    }

    @Test
    void shouldGenerateTimelineFromSimpleStoryboard() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer-desktop", null, "left")),
            new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, "right")),
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("client", "server", "Request"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertEquals(4, timeline.timeline().size());
        assertTrue(timeline.totalDuration() > 0);
    }

    @Test
    void shouldGenerateTimelineWithOnlyEntities() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "center")),
            new CreateEntityCommand("entity3", new CreateEntityParams("Entity 3", null, null, "right"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertEquals(3, timeline.timeline().size());
        long shapeCount = timeline.timeline().stream()
            .filter(e -> e.type().equals("shape"))
            .count();
        assertEquals(3, shapeCount);
    }

    @Test
    void shouldGenerateEmptyTimelineFromEmptyStoryboard() {
        Storyboard storyboard = new Storyboard(List.of());

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertEquals(0, timeline.timeline().size());
        assertEquals(0.0, timeline.totalDuration());
        assertNotNull(timeline.camera());
        assertEquals(0, timeline.camera().size());
    }

    @Test
    void shouldMaintainEventOrderByTime() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", null, null, "left")),
            new CreateEntityCommand("server", new CreateEntityParams("Server", null, null, "right")),
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("client", "server", "Request"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        List<TimelineEvent> events = timeline.timeline();
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(events.get(i).time() <= events.get(i + 1).time(),
                "Events should be ordered by time");
        }
    }

    @Test
    void shouldStartFirstEventAtTimeZero() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(0.0, timeline.timeline().get(0).time());
    }

    @Test
    void shouldSetDefaultCanvasProperties() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline.canvas());
        assertEquals(1280, timeline.canvas().width());
        assertEquals(720, timeline.canvas().height());
        assertEquals("#DDDDDD", timeline.canvas().backgroundColor());
    }

    @Test
    void shouldCalculateCorrectDurationForSingleEntity() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(1.0, timeline.totalDuration());
    }

    @Test
    void shouldCalculateCorrectDurationForMultipleEntities() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(2.0, timeline.totalDuration());
    }

    @Test
    void shouldCalculateCorrectDurationWithConnection() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", null, null, "left")),
            new CreateEntityCommand("server", new CreateEntityParams("Server", null, null, "right")),
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("client", "server", "Request"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(3.5, timeline.totalDuration());
    }

    @Test
    void shouldCalculateCorrectDurationWithPause() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new PauseCommand("pause", new PauseParams(2.5)),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(4.5, timeline.totalDuration());
    }

    @Test
    void shouldHandleComplexStoryboard() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer-desktop", null, "left")),
            new CreateEntityCommand("gateway", new CreateEntityParams("Gateway", "arrows-right-left", null, "center")),
            new PauseCommand("pause1", new PauseParams(1.0)),
            new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "gateway", "Request")),
            new CreateEntityCommand("service1", new CreateEntityParams("Service 1", "server", null, "top-right")),
            new CreateEntityCommand("service2", new CreateEntityParams("Service 2", "server", null, "right")),
            new ConnectEntitiesCommand("conn2", new ConnectEntitiesParams("gateway", "service1", "Route")),
            new ConnectEntitiesCommand("conn3", new ConnectEntitiesParams("gateway", "service2", "Route"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertTrue(timeline.timeline().size() > 0);
        assertTrue(timeline.totalDuration() > 0);
    }

    @Test
    void shouldThrowWhenStoryboardIsInvalid() {
        Storyboard invalidStoryboard = new Storyboard(List.of(
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("nonexistent1", "nonexistent2", "Request"))
        ));

        assertThrows(IllegalArgumentException.class,
            () -> conductorService.generateTimeline(invalidStoryboard));
    }

    @Test
    void shouldThrowWhenStoryboardIsNull() {
        assertThrows(NullPointerException.class,
            () -> conductorService.generateTimeline(null));
    }

    @Test
    void shouldUseCorrectFactoryForEachCommand() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center")),
            new PauseCommand("pause", new PauseParams(1.0)),
            new FocusOnCommand("focus", new FocusOnParams(null, "full_scene", null))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(1, timeline.timeline().size());
        assertEquals(1, timeline.camera().size());
    }

    @Test
    void shouldGenerateShapeEventsForCreateEntityCommands() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertTrue(timeline.timeline().stream()
            .anyMatch(e -> e.type().equals("shape")));
    }

    @Test
    void shouldGenerateArrowEventsForConnectEntitiesCommands() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right")),
            new ConnectEntitiesCommand("connection", new ConnectEntitiesParams("entity1", "entity2", "Connection"))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertTrue(timeline.timeline().stream()
            .anyMatch(e -> e.type().equals("arrow")));
    }

    @Test
    void shouldGenerateCameraEventsForFocusOnCommands() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity", new CreateEntityParams("Entity", null, null, "center")),
            new FocusOnCommand("focus", new FocusOnParams("entity", null, null))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline.camera());
        assertTrue(timeline.camera().size() > 0);
    }

    @Test
    void shouldHandleVeryLongStoryboard() {
        List<Command> commands = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            commands.add(new CreateEntityCommand(
                "entity" + i,
                new CreateEntityParams("Entity " + i, null, null, "center")
            ));
        }

        Storyboard storyboard = new Storyboard(commands);
        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(100, timeline.timeline().size());
        assertEquals(100.0, timeline.totalDuration());
    }

    @Test
    void shouldHandleStoryboardWithOnlyPauses() {
        Storyboard storyboard = new Storyboard(List.of(
            new PauseCommand("pause1", new PauseParams(1.0)),
            new PauseCommand("pause2", new PauseParams(2.0)),
            new PauseCommand("pause3", new PauseParams(1.5))
        ));

        FinalTimeline timeline = conductorService.generateTimeline(storyboard);

        assertEquals(0, timeline.timeline().size());
        assertEquals(4.5, timeline.totalDuration());
    }
}
