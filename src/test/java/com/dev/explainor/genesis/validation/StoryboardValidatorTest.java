/*
package com.dev.explainor.genesis.validation;

import com.dev.explainor.genesis.domain.*;
import com.dev.explainor.genesis.dto.StoryboardV1;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoryboardValidatorTest {

    private final StoryboardValidator validator = new StoryboardValidator();

    @Test
    void shouldReturnValidForCorrectStoryboard() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null)),
            new CreateEntityCommand("e2", new CreateEntityParams("Label", "icon", null, null)),
            new ConnectEntitiesCommand("c1", new ConnectEntitiesParams("e1", "e2", "connect", null))
        ));
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        assertTrue(result.valid());
    }

    @Test
    void shouldDetectDuplicateEntityIds() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null)),
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null))
        ));
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("Duplicate entity ID found: e1"));
    }

    @Test
    void shouldDetectConnectionToNonExistentEntity() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null)),
            new ConnectEntitiesCommand("c1", new ConnectEntitiesParams("e1", "nonexistent", "connect", null))
        ));
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("references non-existent entity: to='nonexistent'"));
    }

    @Test
    void shouldAllowMultipleConnections() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null)),
            new CreateEntityCommand("e2", new CreateEntityParams("Label", "icon", null, null)),
            new ConnectEntitiesCommand("c1", new ConnectEntitiesParams("e1", "e2", "connect1", null)),
            new ConnectEntitiesCommand("c2", new ConnectEntitiesParams("e2", "e1", "connect2", null))
        ));
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        assertTrue(result.valid());
    }
    
    @Test
    void shouldDetectDuplicateCommandIds() {
        StoryboardV1 storyboard = new StoryboardV1("1.0.0", List.of(
            new CreateEntityCommand("e1", new CreateEntityParams("Label", "icon", null, null)),
            new CreateEntityCommand("e2", new CreateEntityParams("Label", "icon", null, null)),
            new ConnectEntitiesCommand("c1", new ConnectEntitiesParams("e1", "e2", "connect", null)),
            new PauseCommand("c1", new PauseParams(1.0))
        ));
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        assertFalse(result.valid());
        assertTrue(result.errorMessage().contains("Duplicate command ID found: c1"));
    }
}
*/

