package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "anthropic.api.key=test-key",
    "renderer.url=http://localhost:3030"
})
class Stage4IntegrationTest {

    @Autowired
    private GenesisConductorService conductorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStage4SimpleStoryboard() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4-simple.storyboard.json");
        
        FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
        
        assertNotNull(timeline);
        assertEquals(FinalTimelineV1.CURRENT_VERSION, timeline.version());
        assertEquals(2, timeline.nodes().size());
        assertEquals(1, timeline.edges().size());
        
        List<AnimationTrack> particleTracks = timeline.tracks().stream()
            .filter(t -> t.type().equals("particle"))
            .toList();
        
        assertEquals(1, particleTracks.size());
        
        AnimationTrack particleTrack = particleTracks.get(0);
        assertTrue(particleTrack.segments().size() > 0);
    }

    @Test
    void testStage4FullStoryboard() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4.storyboard.json");
        
        FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
        
        assertNotNull(timeline);
        assertEquals(3, timeline.nodes().size());
        assertEquals(2, timeline.edges().size());
        
        List<AnimationTrack> particleTracks = timeline.tracks().stream()
            .filter(t -> t.type().equals("particle"))
            .toList();
        
        assertTrue(particleTracks.size() >= 3);
        
        List<AnimationTrack> cameraTracks = timeline.tracks().stream()
            .filter(t -> t.type().equals("camera"))
            .toList();
        
        assertEquals(1, cameraTracks.size());
    }

    @Test
    void testFlowBehaviorGeneratesCorrectPath() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4-simple.storyboard.json");
        
        FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
        
        AnimationTrack particleTrack = timeline.tracks().stream()
            .filter(t -> t.type().equals("particle"))
            .findFirst()
            .orElseThrow();
        
        long positionSegments = particleTrack.segments().stream()
            .filter(s -> s.property().equals("position"))
            .count();
        
        assertTrue(positionSegments > 0);
    }

    @Test
    void testCameraFocusGeneratesCorrectSegments() throws IOException {
        StoryboardV1 storyboard = loadStoryboard("/test-stage4.storyboard.json");
        
        FinalTimelineV1 timeline = conductorService.choreograph(storyboard);
        
        AnimationTrack cameraTrack = timeline.tracks().stream()
            .filter(t -> t.type().equals("camera"))
            .findFirst()
            .orElseThrow();
        
        long cameraPositionSegments = cameraTrack.segments().stream()
            .filter(s -> s.property().equals("cameraPosition"))
            .count();
        
        long zoomSegments = cameraTrack.segments().stream()
            .filter(s -> s.property().equals("zoom"))
            .count();
        
        assertEquals(1, cameraPositionSegments);
        assertEquals(1, zoomSegments);
    }

    private StoryboardV1 loadStoryboard(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            assertNotNull(is, "Resource not found: " + resourcePath);
            return objectMapper.readValue(is, StoryboardV1.class);
        }
    }
}

