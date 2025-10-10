package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.*;
import com.dev.explainor.genesis.layout.GraphBasedLayoutManager;
import com.dev.explainor.genesis.layout.OrthogonalPathFinder;
import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenesisConductorServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LayoutProperties layoutProperties = new LayoutProperties();
    private final ViewportCalculator viewportCalculator = new ViewportCalculator();
    private final GenesisConductorService service = new GenesisConductorService(
        new GraphBasedLayoutManager(layoutProperties),
        new OrthogonalPathFinder(layoutProperties),
        new StoryboardValidator(),
        new LayoutModelFactory(),
        new TimelineFactory(),
        new TimelineEnricher(
            new com.dev.explainor.genesis.timing.DefaultTimingProvider(),
            new BehaviorFactory(),
            new CameraOrchestrator(viewportCalculator)
        )
    );

    @Test
    void shouldGenerateTimelineFromStoryboard() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-stage1.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(json, StoryboardV1.class);

        FinalTimelineV1 timeline = service.choreograph(storyboard);

        assertNotNull(timeline);
        assertEquals(FinalTimelineV1.CURRENT_VERSION, timeline.version());
        assertEquals(1280, timeline.stage().width());
        assertEquals(720, timeline.stage().height());
        assertEquals(2, timeline.nodes().size());
        assertEquals(1, timeline.edges().size());
    }

    @Test
    void shouldGenerateDeterministicCoordinates() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-stage1.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(json, StoryboardV1.class);

        FinalTimelineV1 timeline1 = service.choreograph(storyboard);
        FinalTimelineV1 timeline2 = service.choreograph(storyboard);

        assertEquals(timeline1.nodes().get(0).x(), timeline2.nodes().get(0).x());
        assertEquals(timeline1.nodes().get(0).y(), timeline2.nodes().get(0).y());
        assertEquals(timeline1.nodes().get(1).x(), timeline2.nodes().get(1).x());
        assertEquals(timeline1.nodes().get(1).y(), timeline2.nodes().get(1).y());
    }

    @Test
    void shouldRejectNullStoryboard() {
        assertThrows(NullPointerException.class, () -> service.choreograph(null));
    }

    @Test
    void shouldRejectInvalidVersion() {
        StoryboardV1 invalidStoryboard = new StoryboardV1("999.0.0", List.of());
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> service.choreograph(invalidStoryboard)
        );
        
        assertTrue(exception.getMessage().contains("Unsupported storyboard version"));
    }
}

