package com.dev.explainor.conductor.validation;

import com.dev.explainor.conductor.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoryboardValidatorTest {

    private StoryboardValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StoryboardValidator();
    }

    @Test
    void shouldValidateValidStoryboard() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right")),
            new ConnectEntitiesCommand("conn", new ConnectEntitiesParams("entity1", "entity2", "Connection"))
        ));

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }

    @Test
    void shouldValidateStoryboardWithOnlyCreateCommands() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }

    @Test
    void shouldValidateStoryboardWithPause() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new PauseCommand("pause1", new PauseParams(1.0)),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }

    @Test
    void shouldValidateStoryboardWithFocusOn() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new FocusOnCommand("focus1", new FocusOnParams("entity1", null, null))
        ));

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }

    @Test
    void shouldThrowWhenConnectingNonExistentSourceEntity() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new ConnectEntitiesCommand("conn", new ConnectEntitiesParams("nonexistent", "entity1", "Connection"))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("nonexistent"));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void shouldThrowWhenConnectingNonExistentTargetEntity() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new ConnectEntitiesCommand("conn", new ConnectEntitiesParams("entity1", "nonexistent", "Connection"))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    @Test
    void shouldThrowWhenFocusingOnNonExistentEntity() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new FocusOnCommand("focus1", new FocusOnParams("nonexistent", null, null))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    @Test
    void shouldThrowWhenConnectingBeforeCreating() {
        Storyboard storyboard = new Storyboard(List.of(
            new ConnectEntitiesCommand("conn", new ConnectEntitiesParams("entity1", "entity2", "Connection")),
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity2", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("entity1") || exception.getMessage().contains("entity2"));
    }

    @Test
    void shouldThrowWhenDuplicateEntityIdsExist() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 1", null, null, "left")),
            new CreateEntityCommand("entity1", new CreateEntityParams("Entity 2", null, null, "right"))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("entity1"));
        assertTrue(exception.getMessage().contains("duplicate") || exception.getMessage().contains("already exists"));
    }

    @Test
    void shouldThrowWhenPauseDurationIsNegative() {
        Storyboard storyboard = new Storyboard(List.of(
            new PauseCommand("pause1", new PauseParams(-1.0))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("duration"));
    }

    @Test
    void shouldThrowWhenPauseDurationIsZero() {
        Storyboard storyboard = new Storyboard(List.of(
            new PauseCommand("pause1", new PauseParams(0.0))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("duration"));
    }

    @Test
    void shouldThrowWhenFocusOnHasNoTarget() {
        Storyboard storyboard = new Storyboard(List.of(
            new FocusOnCommand("focus1", new FocusOnParams(null, null, null))
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate(storyboard)
        );

        assertTrue(exception.getMessage().contains("target") || exception.getMessage().contains("area") || exception.getMessage().contains("scale"));
    }

    @Test
    void shouldValidateEmptyStoryboard() {
        Storyboard storyboard = new Storyboard(List.of());

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }

    @Test
    void shouldThrowWhenStoryboardIsNull() {
        assertThrows(NullPointerException.class, () -> validator.validate(null));
    }

    @Test
    void shouldValidateComplexStoryboard() {
        Storyboard storyboard = new Storyboard(List.of(
            new CreateEntityCommand("client", new CreateEntityParams("Client", "computer-desktop", null, "left")),
            new CreateEntityCommand("gateway", new CreateEntityParams("Gateway", "arrows-right-left", null, "center")),
            new PauseCommand("pause1", new PauseParams(1.0)),
            new ConnectEntitiesCommand("conn1", new ConnectEntitiesParams("client", "gateway", "Request")),
            new PauseCommand("pause2", new PauseParams(0.5)),
            new CreateEntityCommand("service1", new CreateEntityParams("Service 1", "server", null, "top-right")),
            new CreateEntityCommand("service2", new CreateEntityParams("Service 2", "server", null, "right")),
            new CreateEntityCommand("service3", new CreateEntityParams("Service 3", "server", null, "bottom-right")),
            new ConnectEntitiesCommand("conn2", new ConnectEntitiesParams("gateway", "service1", "Route")),
            new ConnectEntitiesCommand("conn3", new ConnectEntitiesParams("gateway", "service2", "Route")),
            new ConnectEntitiesCommand("conn4", new ConnectEntitiesParams("gateway", "service3", "Route")),
            new FocusOnCommand("focus1", new FocusOnParams(null, "full_scene", null))
        ));

        assertDoesNotThrow(() -> validator.validate(storyboard));
    }
}

