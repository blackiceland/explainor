package com.dev.explainor.genesis.timing;

import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.CreateEntityParams;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesParams;
import com.dev.explainor.genesis.domain.PauseCommand;
import com.dev.explainor.genesis.domain.PauseParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultTimingProviderTest {

    private final DefaultTimingProvider provider = new DefaultTimingProvider();

    @Test
    void shouldCalculateTimingForCreateEntity() {
        CreateEntityCommand command = new CreateEntityCommand(
            "entity1",
            new CreateEntityParams("Test", "icon", "rectangle", "center")
        );
        TimelineContext context = TimelineContext.initial();

        TimingInfo timing = provider.calculateTiming(command, context);

        assertEquals(0.0, timing.startTime());
        assertEquals(1.0, timing.duration());
        assertEquals("easeInOutQuint", timing.easing());
    }

    @Test
    void shouldCalculateTimingForConnectEntities() {
        ConnectEntitiesCommand command = new ConnectEntitiesCommand(
            "conn1",
            new ConnectEntitiesParams("from", "to", "label")
        );
        TimelineContext context = TimelineContext.initial();

        TimingInfo timing = provider.calculateTiming(command, context);

        assertEquals(0.0, timing.startTime());
        assertEquals(1.5, timing.duration());
        assertEquals("easeInOutCubic", timing.easing());
    }

    @Test
    void shouldCalculateTimingForPause() {
        PauseCommand command = new PauseCommand("pause1", new PauseParams(2.5));
        TimelineContext context = TimelineContext.initial();

        TimingInfo timing = provider.calculateTiming(command, context);

        assertEquals(0.0, timing.startTime());
        assertEquals(2.5, timing.duration());
    }

    @Test
    void shouldApplyStaggerDelay() {
        CreateEntityCommand command1 = new CreateEntityCommand(
            "entity1",
            new CreateEntityParams("Test1", "icon", "rectangle", "center")
        );
        CreateEntityCommand command2 = new CreateEntityCommand(
            "entity2",
            new CreateEntityParams("Test2", "icon", "rectangle", "center")
        );

        TimelineContext context = TimelineContext.initial();
        TimingInfo timing1 = provider.calculateTiming(command1, context);
        
        context.incrementIndex();
        TimingInfo timing2 = provider.calculateTiming(command2, context);

        assertEquals(0.0, timing1.startTime());
        assertEquals(0.15, timing2.startTime());
    }

    @Test
    void shouldAdvanceContextTime() {
        TimelineContext context = TimelineContext.initial();
        assertEquals(0.0, context.getCurrentTime());

        context.advance(1.5);
        assertEquals(1.5, context.getCurrentTime());

        context.advance(2.0);
        assertEquals(3.5, context.getCurrentTime());
    }
}

