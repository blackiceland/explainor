package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.FocusOnCommand;
import com.dev.explainor.conductor.domain.FocusOnParams;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.service.SceneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FocusOnFactoryTest {

    private FocusOnFactory factory;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        factory = new FocusOnFactory();
        sceneState = new SceneState(1280, 720);
    }

    @Test
    void shouldCreateCameraEventForCenter() {
        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("center", null, 1.0)
        );

        var events = factory.createTimelineEvents(command, sceneState);

        assertNotNull(events);
        assertEquals(1, events.size());
        
        var event = events.get(0);
        assertEquals("camera", event.elementId());
        assertEquals("camera", event.type());
        assertEquals("move", event.action());
    }

    @Test
    void shouldFocusOnExistingEntity() {
        sceneState.registerEntity(new SceneEntity("client", 320, 360, 200, 120));

        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("client", null, 1.5)
        );

        var events = factory.createTimelineEvents(command, sceneState);
        var event = events.get(0);

        assertEquals(320.0, event.props().get("x"));
        assertEquals(360.0, event.props().get("y"));
        assertEquals(1.5, event.props().get("scale"));
    }

    @Test
    void shouldUseDefaultScaleWhenNull() {
        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("center", null, null)
        );

        var events = factory.createTimelineEvents(command, sceneState);
        var event = events.get(0);

        assertEquals(1.0, event.props().get("scale"));
    }

    @Test
    void shouldFallbackToCenterForNonexistentEntity() {
        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("nonexistent", null, 1.0)
        );

        var events = factory.createTimelineEvents(command, sceneState);
        var event = events.get(0);

        assertEquals(640.0, event.props().get("x"));
        assertEquals(360.0, event.props().get("y"));
    }

    @Test
    void shouldAdvanceTimeAfterCreation() {
        double initialTime = sceneState.getCurrentTime();

        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("center", null, 1.0)
        );

        factory.createTimelineEvents(command, sceneState);

        assertTrue(sceneState.getCurrentTime() > initialTime);
        assertEquals(1.0, sceneState.getCurrentTime() - initialTime);
    }

    @Test
    void shouldSupportFocusOnCommand() {
        FocusOnCommand command = new FocusOnCommand(
            "focus1",
            new FocusOnParams("center", null, 1.0)
        );

        assertTrue(factory.supports(command));
    }
}

