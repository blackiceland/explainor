package com.dev.explainor.genesis.validation;

import com.dev.explainor.genesis.dto.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoryboardValidatorTest {

    private final StoryboardValidator validator = new StoryboardValidator();

    @Test
    void shouldPassValidStoryboard() {
        StoryboardV1 storyboard = new StoryboardV1(
            "1.0.0",
            List.of(
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", "left")),
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", "right")),
                new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertTrue(result.valid());
        assertNull(result.errorMessage());
    }

    @Test
    void shouldRejectDuplicateCommandId() {
        StoryboardV1 storyboard = new StoryboardV1(
            "1.0.0",
            List.of(
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", "left")),
                new CreateEntityCommand("client", new CreateEntityParams("Server", "server", "right"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("Duplicate command ID: client"));
    }

    @Test
    void shouldRejectNonExistentFromNode() {
        StoryboardV1 storyboard = new StoryboardV1(
            "1.0.0",
            List.of(
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", "right")),
                new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("references non-existent entity: from='client'"));
    }

    @Test
    void shouldRejectNonExistentToNode() {
        StoryboardV1 storyboard = new StoryboardV1(
            "1.0.0",
            List.of(
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", "left")),
                new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("references non-existent entity: to='server'"));
    }

    @Test
    void shouldAllowMultipleConnectionsBetweenSameNodes() {
        StoryboardV1 storyboard = new StoryboardV1(
            "1.0.0",
            List.of(
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", "left")),
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", "right")),
                new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP")),
                new ConnectEntitiesCommand("conn2", new ConnectEntitiesParams("client", "server", "WebSocket"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertTrue(result.valid());
    }
}

