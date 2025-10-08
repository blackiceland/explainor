package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.GraphBasedLayoutManager;
import com.dev.explainor.genesis.layout.OrthogonalPathFinder;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GenesisConductorServiceGraphTest {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final GenesisConductorService service = new GenesisConductorService(
        new GraphBasedLayoutManager(new LayoutProperties()),
        new OrthogonalPathFinder(new LayoutProperties()),
        new StoryboardValidator(),
        new LayoutModelFactory(),
        new TimelineFactory()
    );

    @Test
    void chainShouldProduceHierarchicalLayout() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-chain.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(json, StoryboardV1.class);
        FinalTimelineV1 timeline = service.choreograph(storyboard);
        assertEquals(3, timeline.nodes().size());
        assertEquals(2, timeline.edges().size());
        assertTrue(timeline.nodes().stream().anyMatch(n -> n.id().equals("A")));
        assertTrue(timeline.nodes().stream().anyMatch(n -> n.id().equals("B")));
        assertTrue(timeline.nodes().stream().anyMatch(n -> n.id().equals("C")));
    }

    @Test
    void treeShouldPlaceChildrenOnSameLevel() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-tree.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(json, StoryboardV1.class);
        FinalTimelineV1 timeline = service.choreograph(storyboard);
        assertEquals(3, timeline.nodes().size());
        double yA = timeline.nodes().stream().filter(n -> n.id().equals("A")).findFirst().orElseThrow().y();
        double yB = timeline.nodes().stream().filter(n -> n.id().equals("B")).findFirst().orElseThrow().y();
        double yC = timeline.nodes().stream().filter(n -> n.id().equals("C")).findFirst().orElseThrow().y();
        assertTrue(yB >= yA);
        assertTrue(yC >= yA);
    }

    @Test
    void obstacleShouldRouteAroundNode() throws Exception {
        String json = Files.readString(Paths.get("src/test/resources/test-obstacle.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(json, StoryboardV1.class);
        FinalTimelineV1 timeline = service.choreograph(storyboard);
        assertEquals(1, timeline.edges().size());
        assertTrue(timeline.edges().get(0).path().size() >= 2);
    }
}


