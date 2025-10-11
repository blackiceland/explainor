/*
package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.AStarPathSolver;
import com.dev.explainor.genesis.layout.GraphBasedLayoutManager;
import com.dev.explainor.genesis.layout.GridBuilder;
import com.dev.explainor.genesis.layout.PathFindingCoordinator;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldenTimelineTest {

    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final LayoutProperties layoutProperties = new LayoutProperties();
    private final ViewportCalculator viewportCalculator = new ViewportCalculator();
    private final GenesisConductorService service = new GenesisConductorService(
        new GraphBasedLayoutManager(layoutProperties),
        new PathFindingCoordinator(new GridBuilder(layoutProperties), new AStarPathSolver(50, 10000)),
        new StoryboardValidator(),
        new LayoutModelFactory(),
        new TimelineFactory(),
        new TimelineEnricher(
            new com.dev.explainor.genesis.timing.DefaultTimingProvider(),
            new AnimationTrackFactory()
        )
    );

    @Test
    void testStage1DeterminismWithGoldenFile() throws Exception {
        String storyboardJson = Files.readString(Paths.get("src/test/resources/test-stage1.storyboard.json"));
        StoryboardV1 storyboard = objectMapper.readValue(storyboardJson, StoryboardV1.class);

        FinalTimelineV1 timeline = service.choreograph(storyboard);

        Path goldenFilePath = Paths.get("src/test/resources/test-stage1.golden.timeline.json");
        String goldenJson = Files.readString(goldenFilePath);
        
        String actualJson = objectMapper.writeValueAsString(timeline);

        assertEquals(
            objectMapper.readTree(goldenJson),
            objectMapper.readTree(actualJson)
        );
    }
}
*/

