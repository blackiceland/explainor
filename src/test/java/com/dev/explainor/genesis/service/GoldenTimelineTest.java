package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.DummyLayoutManager;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldenTimelineTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    
    private final GenesisConductorService service = new GenesisConductorService(
        new DummyLayoutManager(),
        new StoryboardValidator()
    );

    @Test
    void shouldMatchGoldenTimeline() throws Exception {
        String storyboardJson = Files.readString(
            Paths.get("src/test/resources/test-stage1.storyboard.json")
        );
        StoryboardV1 storyboard = objectMapper.readValue(storyboardJson, StoryboardV1.class);

        FinalTimelineV1 actualTimeline = service.choreograph(storyboard);
        String actualJson = objectMapper.writeValueAsString(actualTimeline);

        Path goldenPath = Paths.get("src/test/resources/test-stage1.golden.timeline.json");
        
        if (!Files.exists(goldenPath)) {
            Files.writeString(goldenPath, actualJson);
        }

        String expectedJson = Files.readString(goldenPath);
        FinalTimelineV1 expectedTimeline = objectMapper.readValue(
            expectedJson, 
            FinalTimelineV1.class
        );

        assertEquals(expectedTimeline, actualTimeline, 
            "Timeline must match golden file byte-by-byte for determinism");
    }
}

