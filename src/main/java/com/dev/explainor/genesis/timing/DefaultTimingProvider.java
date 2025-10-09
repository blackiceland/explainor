package com.dev.explainor.genesis.timing;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.PauseCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTimingProvider implements TimingProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultTimingProvider.class);
    
    private static final double CREATE_ENTITY_DURATION = 1.0;
    private static final double CONNECT_ENTITIES_DURATION = 1.5;
    private static final double STAGGER_DELAY = 0.1;

    @Override
    public TimingInfo calculateTiming(Command command, TimelineContext context) {
        double startTime = context.getCurrentTime();
        double duration = getDuration(command);
        double staggerOffset = calculateStaggerOffset(context);
        
        double adjustedStartTime = startTime + staggerOffset;
        String easing = getEasing(command);
        
        log.debug("Calculated timing for command {}: startTime={}, duration={}, easing={}", 
            command.getClass().getSimpleName(), adjustedStartTime, duration, easing);
        
        return TimingInfo.of(adjustedStartTime, duration, easing);
    }

    private double getDuration(Command command) {
        if (command instanceof CreateEntityCommand) {
            return CREATE_ENTITY_DURATION;
        } else if (command instanceof ConnectEntitiesCommand) {
            return CONNECT_ENTITIES_DURATION;
        } else if (command instanceof PauseCommand pauseCommand) {
            return pauseCommand.params().duration();
        }
        return 1.0;
    }

    private double calculateStaggerOffset(TimelineContext context) {
        return context.getElementIndex() * STAGGER_DELAY;
    }

    private String getEasing(Command command) {
        if (command instanceof ConnectEntitiesCommand) {
            return "easeInOutCubic";
        }
        return "easeInOutQuint";
    }
}

