/*
package com.dev.explainor.genesis.api;

import com.dev.explainor.genesis.domain.*;
import com.dev.explainor.genesis.dto.*;
import com.dev.explainor.genesis.service.GenesisConductorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenesisController.class)
class GenesisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenesisConductorService conductorService;

    @Test
    void shouldReturnSuccessfulTimelineForValidStoryboard() throws Exception {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Label", "icon", null, null))
        ));

        FinalTimelineV1 expectedTimeline = new FinalTimelineV1(
            "1.1.0",
            new Stage(1280, 720),
            List.of(new TimelineNode("entity1", "Label", "icon", 0.0, 0.0, VisualStyle.defaultNodeStyle())),
            List.of(),
            List.of()
        );

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenReturn(expectedTimeline);

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storyboard)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").value("1.1.0"))
            .andExpect(jsonPath("$.stage.width").value(1280))
            .andExpect(jsonPath("$.stage.height").value(720))
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.nodes[0].id").value("entity1"))
            .andExpect(jsonPath("$.edges").isArray());
    }

    @Test
    void shouldReturnBadRequestForInvalidVersion() throws Exception {
        StoryboardV1 invalidStoryboard = new StoryboardV1("999.0.0", List.of());

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenThrow(new IllegalArgumentException("Unsupported storyboard version: 999.0.0"));

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStoryboard)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Unsupported storyboard version: 999.0.0"));
    }

    @Test
    void shouldReturnBadRequestForMissingVersion() throws Exception {
        String invalidJson = """
            {
              "commands": []
            }
            """;

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForNullCommands() throws Exception {
        String invalidJson = """
            {
              "version": "1.0.0",
              "commands": null
            }
            """;

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidStoryboard() throws Exception {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("nonexistent1", "nonexistent2", null, null))
        ));

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenThrow(new IllegalArgumentException("Invalid storyboard: ConnectEntitiesCommand 'conn1' references non-existent entity: from='nonexistent1'"));

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storyboard)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldHandleComplexStoryboard() throws Exception {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", null, null)),
            new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, null)),
            new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP", null)),
            new PauseCommand("pause1", new PauseParams(1.0))
        ));

        FinalTimelineV1 expectedTimeline = new FinalTimelineV1(
            "1.1.0",
            new Stage(1280, 720),
            List.of(
                new TimelineNode("client", "Client", "computer", -100.0, 0.0, VisualStyle.defaultNodeStyle()),
                new TimelineNode("server", "Server", "server", 100.0, 0.0, VisualStyle.defaultNodeStyle())
            ),
            List.of(
                new TimelineEdge("conn1", "client", "server", "HTTP", List.of(new Point(0,0), new Point(100,0)), EdgeStyle.defaultEdgeStyle(), 100.0)
            ),
            List.of()
        );

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenReturn(expectedTimeline);

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storyboard)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.nodes.length()").value(2))
            .andExpect(jsonPath("$.edges").isArray())
            .andExpect(jsonPath("$.edges.length()").value(1))
            .andExpect(jsonPath("$.edges[0].label").value("HTTP"));
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/genesis/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("ok"))
            .andExpect(jsonPath("$.storyboardVersion").value("1.0.0"))
            .andExpect(jsonPath("$.timelineVersion").value("1.1.0"));
    }

    @Test
    void shouldReturn500OnUnexpectedError() throws Exception {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of());

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenThrow(new RuntimeException("Unexpected database error"));

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storyboard)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void shouldAcceptEmptyCommandsList() throws Exception {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of());

        FinalTimelineV1 expectedTimeline = new FinalTimelineV1(
            "1.1.0",
            new Stage(1280, 720),
            List.of(),
            List.of(),
            List.of()
        );

        when(conductorService.choreograph(any(StoryboardV1.class)))
            .thenReturn(expectedTimeline);

        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storyboard)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.nodes").isEmpty())
            .andExpect(jsonPath("$.edges").isArray())
            .andExpect(jsonPath("$.edges").isEmpty());
    }

    @Test
    void shouldRequireContentType() throws Exception {
        mockMvc.perform(post("/api/genesis/choreograph")
                .content("{}"))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/api/genesis/choreograph")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"version\": \"1.0.0\", \"commands\": null ")) // Malformed JSON (missing closing brace)
            .andExpect(status().isBadRequest());
    }
}
*/

