package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.PauseCommand;
import com.dev.explainor.conductor.domain.PauseParams;
import com.dev.explainor.conductor.service.SceneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PauseFactoryTest {

    private PauseFactory factory;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        factory = new PauseFactory();
        sceneState = new SceneState(1280, 720);
    }

    @Test
    void shouldAdvanceTimeByDuration() {
        double initialTime = sceneState.getCurrentTime();
        
        PauseCommand command = new PauseCommand(
            "pause1",
            new PauseParams(2.5)
        );

        factory.createTimelineEvents(command, sceneState);

        assertEquals(initialTime + 2.5, sceneState.getCurrentTime());
    }

    @Test
    void shouldReturnEmptyEventsList() {
        PauseCommand command = new PauseCommand(
            "pause1",
            new PauseParams(1.0)
        );

        var events = factory.createTimelineEvents(command, sceneState);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void shouldHandleZeroDuration() {
        double initialTime = sceneState.getCurrentTime();
        
        PauseCommand command = new PauseCommand(
            "pause1",
            new PauseParams(0.0)
        );

        factory.createTimelineEvents(command, sceneState);

        assertEquals(initialTime, sceneState.getCurrentTime());
    }

    @Test
    void shouldSupportPauseCommand() {
        PauseCommand command = new PauseCommand(
            "pause1",
            new PauseParams(1.0)
        );

        assertTrue(factory.supports(command));
    }
}

