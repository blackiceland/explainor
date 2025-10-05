package com.dev.explainor.conductor.service;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.domain.Storyboard;
import com.dev.explainor.conductor.factory.CommandFactory;
import com.dev.explainor.conductor.factory.CreateEntityFactory;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.SimpleHintLayoutManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConductorServiceFailureTest {

    private ConductorService conductorService;

    @BeforeEach
    void setUp() {
        LayoutManager layoutManager = new SimpleHintLayoutManager();
        List<CommandFactory> factories = List.of(new CreateEntityFactory(layoutManager));
        conductorService = new ConductorService(layoutManager, factories);
    }

    @Test
    void shouldThrowExceptionForNullStoryboard() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> conductorService.generateTimeline(null)
        );

        assertTrue(exception.getMessage().contains("Storyboard cannot be null"));
    }

    @Test
    void shouldThrowExceptionForUnsupportedCommand() {
        Command unsupportedCommand = new Command() {
            @Override
            public String id() {
                return "unsupported";
            }
        };

        Storyboard storyboard = new Storyboard(List.of(unsupportedCommand));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> conductorService.generateTimeline(storyboard)
        );

        assertTrue(exception.getMessage().contains("No factory found for command type"));
    }

    @Test
    void shouldSuccessfullyProcessSupportedCommands() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Label1", null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Label2", null, "right"))
        ));

        var timeline = conductorService.generateTimeline(storyboard);

        assertNotNull(timeline);
        assertEquals(2.0, timeline.totalDuration());
        assertEquals(6, timeline.timeline().size());
    }
}

