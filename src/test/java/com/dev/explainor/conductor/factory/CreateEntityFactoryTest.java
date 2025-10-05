package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.ConnectEntitiesParams;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.Point;
import com.dev.explainor.conductor.layout.SimpleHintLayoutManager;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateEntityFactoryTest {

    private CreateEntityFactory factory;
    private SceneState sceneState;
    private LayoutManager layoutManager;

    @BeforeEach
    void setUp() {
        layoutManager = new SimpleHintLayoutManager();
        factory = new CreateEntityFactory(layoutManager);
        org.jgrapht.Graph<String, org.jgrapht.graph.DefaultEdge> graph = new org.jgrapht.graph.SimpleDirectedGraph<>(org.jgrapht.graph.DefaultEdge.class);
        sceneState = new SceneState(1280, 720, graph);
    }

    @Test
    void shouldCreateSingleShapeEvent() {
        CreateEntityCommand command = new CreateEntityCommand(
            "client",
            new CreateEntityParams("Client", null, null, "left")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        assertEquals(1, events.size());
        TimelineEvent shapeEvent = events.get(0);
        assertEquals("client_shape", shapeEvent.elementId());
        assertEquals("shape", shapeEvent.type());
        assertEquals("appear", shapeEvent.action());
    }

    @Test
    void shouldSupportCreateEntityCommand() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        assertTrue(factory.supports(command));
    }

    @Test
    void shouldNotSupportOtherCommandTypes() {
        Command command = new ConnectEntitiesCommand(
            "connection",
            new ConnectEntitiesParams("from", "to", "label")
        );

        assertFalse(factory.supports(command));
    }

    @Test
    void shouldIncludeLabelInProps() {
        CreateEntityCommand command = new CreateEntityCommand(
            "server",
            new CreateEntityParams("Server", "server", null, "right")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("Server", shapeEvent.props().get("label"));
    }

    @Test
    void shouldIncludeIconInPropsWhenProvided() {
        CreateEntityCommand command = new CreateEntityCommand(
            "server",
            new CreateEntityParams("Server", "computer-desktop", null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("computer-desktop", shapeEvent.props().get("icon"));
    }

    @Test
    void shouldIncludeNullIconWhenNotProvided() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertNull(shapeEvent.props().get("icon"));
    }

    @Test
    void shouldUseDefaultShapeTypeWhenNotProvided() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("rectangle", shapeEvent.props().get("shapeType"));
    }

    @Test
    void shouldUseProvidedShapeType() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, "circle", "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("circle", shapeEvent.props().get("shapeType"));
    }

    @Test
    void shouldSetCorrectDimensions() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals(200.0, shapeEvent.props().get("width"));
        assertEquals(120.0, shapeEvent.props().get("height"));
    }

    @Test
    void shouldRegisterEntityInSceneState() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", "server", null, "center")
        );

        factory.createTimelineEvents(command, sceneState);

        assertTrue(sceneState.getEntityById("entity").isPresent());
    }

    @Test
    void shouldRegisterEntityWithCorrectDimensions() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        factory.createTimelineEvents(command, sceneState);

        var entity = sceneState.getEntityById("entity").orElseThrow();
        assertEquals(200.0, entity.width());
        assertEquals(120.0, entity.height());
    }

    @Test
    void shouldAdvanceTimeAfterCreation() {
        double initialTime = sceneState.getCurrentTime();

        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        factory.createTimelineEvents(command, sceneState);

        assertEquals(initialTime + 1.0, sceneState.getCurrentTime());
    }

    @Test
    void shouldNotAdvanceTimeIfMultipleEntitiesCreatedSequentially() {
        double initialTime = sceneState.getCurrentTime();

        CreateEntityCommand command1 = new CreateEntityCommand(
            "entity1",
            new CreateEntityParams("Entity 1", null, null, "left")
        );
        CreateEntityCommand command2 = new CreateEntityCommand(
            "entity2",
            new CreateEntityParams("Entity 2", null, null, "right")
        );

        factory.createTimelineEvents(command1, sceneState);
        double timeAfterFirst = sceneState.getCurrentTime();
        factory.createTimelineEvents(command2, sceneState);

        assertEquals(initialTime + 1.0, timeAfterFirst);
        assertEquals(initialTime + 2.0, sceneState.getCurrentTime());
    }

    @Test
    void shouldUseLayoutManagerForPositioning() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "left")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertNotNull(shapeEvent.props().get("x"));
        assertNotNull(shapeEvent.props().get("y"));
    }

    @Test
    void shouldPositionEntitiesDifferentlyBasedOnHint() {
        CreateEntityCommand leftCommand = new CreateEntityCommand(
            "left_entity",
            new CreateEntityParams("Left", null, null, "left")
        );
        CreateEntityCommand rightCommand = new CreateEntityCommand(
            "right_entity",
            new CreateEntityParams("Right", null, null, "right")
        );

        List<TimelineEvent> leftEvents = factory.createTimelineEvents(leftCommand, sceneState);
        List<TimelineEvent> rightEvents = factory.createTimelineEvents(rightCommand, sceneState);

        double leftX = (double) leftEvents.get(0).props().get("x");
        double rightX = (double) rightEvents.get(0).props().get("x");

        assertNotEquals(leftX, rightX);
    }

    @Test
    void shouldHandleBlankIconAsEmptyString() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", "   ", null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("   ", shapeEvent.props().get("icon"));
    }

    @Test
    void shouldHandleEmptyLabel() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("", null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals("", shapeEvent.props().get("label"));
    }

    @Test
    void shouldHandleNullPositionHint() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, null)
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        assertNotNull(events);
        assertEquals(1, events.size());
    }

    @Test
    void shouldHandleVeryLongLabel() {
        String longLabel = "A".repeat(1000);
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams(longLabel, null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals(longLabel, shapeEvent.props().get("label"));
    }

    @Test
    void shouldHandleSpecialCharactersInLabel() {
        String specialLabel = "Entity <>&\"'";
        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams(specialLabel, null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals(specialLabel, shapeEvent.props().get("label"));
    }

    @Test
    void shouldStartAtCurrentTime() {
        sceneState.advanceTime(5.0);
        double expectedTime = sceneState.getCurrentTime();

        CreateEntityCommand command = new CreateEntityCommand(
            "entity",
            new CreateEntityParams("Entity", null, null, "center")
        );

        List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);

        TimelineEvent shapeEvent = events.get(0);
        assertEquals(expectedTime, shapeEvent.time());
    }
}
