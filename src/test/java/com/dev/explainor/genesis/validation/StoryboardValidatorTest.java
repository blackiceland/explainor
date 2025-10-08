package com.dev.explainor.genesis.validation;

import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesParams;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.CreateEntityParams;
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
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", null, "left")),
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, "right")),
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
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", null, "left")),
                new CreateEntityCommand("client", new CreateEntityParams("Server", "server", null, "right"))
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
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, "right")),
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
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", null, "left")),
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
                new CreateEntityCommand("client", new CreateEntityParams("Client", "computer", null, "left")),
                new CreateEntityCommand("server", new CreateEntityParams("Server", "server", null, "right")),
                new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "server", "HTTP")),
                new ConnectEntitiesCommand("conn2", new ConnectEntitiesParams("client", "server", "WebSocket"))
            )
        );

        StoryboardValidator.ValidationResult result = validator.validate(storyboard);

        assertTrue(result.valid());
    }
}

