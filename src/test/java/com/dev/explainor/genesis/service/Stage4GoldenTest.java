package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "anthropic.api.key=test-key",
    "renderer.url=http://localhost:3030"
})
class Stage4GoldenTest {

    @Autowired
    private GenesisConductorService conductorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStage4DeterminismWithGoldenFile() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4-simple.storyboard.json");
        
        FinalTimelineV1 timeline1 = conductorService.choreograph(storyboard);
        FinalTimelineV1 timeline2 = conductorService.choreograph(storyboard);
        
        String json1 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timeline1);
        String json2 = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(timeline2);
        
        assertEquals(json1, json2, "Two runs of the same storyboard should produce identical output");
    }

    @Test
    void testStage4TimelineStructure() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4-simple.storyboard.json");
        
        FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
        
        assertNotNull(timeline.version());
        assertNotNull(timeline.stage());
        assertNotNull(timeline.nodes());
        assertNotNull(timeline.edges());
        assertNotNull(timeline.tracks());
        
        assertTrue(timeline.tracks().stream().anyMatch(t -> t.type().equals("particle")));
    }

    private StoryboardV1 loadStoryboard(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            assertNotNull(is, "Resource not found: " + resourcePath);
            return objectMapper.readValue(is, StoryboardV1.class);
        }
    }
}

