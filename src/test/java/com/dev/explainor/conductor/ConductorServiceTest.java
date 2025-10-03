package com.dev.explainor.conductor;

import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.ConnectEntitiesParams;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.domain.Storyboard;
import com.dev.explainor.conductor.factory.CommandFactory;
import com.dev.explainor.conductor.factory.ConnectEntitiesFactory;
import com.dev.explainor.conductor.factory.CreateEntityFactory;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.SimpleHintLayoutManager;
import com.dev.explainor.conductor.service.ConductorService;
import com.dev.explainor.renderer.domain.FinalTimeline;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConductorServiceTest {

    private ConductorService conductorService;

    @BeforeEach
    void setUp() {
        LayoutManager layoutManager = new SimpleHintLayoutManager();
        List<CommandFactory> factories = List.of(
            new CreateEntityFactory(layoutManager),
            new ConnectEntitiesFactory()
        );
        conductorService = new ConductorService(layoutManager, factories);
    }

    @Test
    void testGenerateTimeline() {
        // 1. Arrange: Create a sample storyboard
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", null, "left")),
            new CreateEntityCommand("server", new CreateEntityParams("Server", null, "right")),
            new ConnectEntitiesCommand("req", new ConnectEntitiesParams("client", "server", "Request"))
        ));

        // 2. Act: Generate the timeline
        FinalTimeline finalTimeline = conductorService.generateTimeline(storyboard);

        // 3. Assert: Verify the output
        assertNotNull(finalTimeline);
        assertNotNull(finalTimeline.canvas());
        assertEquals(1280, finalTimeline.canvas().width());

        // Expected total duration: 1.0s (client) + 1.0s (server) + 1.5s (arrow) = 3.5s
        assertEquals(3.5, finalTimeline.totalDuration());

        List<TimelineEvent> events = finalTimeline.timeline();
        assertEquals(3 + 3 + 2, events.size()); // client (3) + server(3) + arrow (2)

        // Verify Client entity events
        Optional<TimelineEvent> clientGroup = findEvent(events, "client_group");
        assertTrue(clientGroup.isPresent());
        assertEquals(0.0, clientGroup.get().time());
        assertEquals("group", clientGroup.get().type());

        // Verify Server entity events
        Optional<TimelineEvent> serverGroup = findEvent(events, "server_group");
        assertTrue(serverGroup.isPresent());
        assertEquals(1.0, serverGroup.get().time()); // Should appear after client

        // Verify Arrow events
        Optional<TimelineEvent> arrow = findEvent(events, "req_arrow");
        assertTrue(arrow.isPresent());
        assertEquals(2.0, arrow.get().time()); // Should appear after server
        assertEquals("animate", arrow.get().action());
        assertEquals(1.5, arrow.get().duration());
    }

    private Optional<TimelineEvent> findEvent(List<TimelineEvent> events, String elementId) {
        return events.stream().filter(e -> e.elementId().equals(elementId)).findFirst();
    }
}
