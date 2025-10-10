package com.dev.explainor.genesis.timing;

import com.dev.explainor.genesis.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class DefaultTimingProvider implements TimingProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultTimingProvider.class);
    
    private static final double CREATE_ENTITY_DURATION = 1.0;
    private static final double CONNECT_ENTITIES_DURATION = 1.5;
    private static final double ANIMATE_BEHAVIOR_DURATION = 2.0;
    private static final double FOCUS_ON_DURATION = 1.5;
    private static final double STAGGER_DELAY = 0.15;

    private static final Map<String, String> EASING_MAP = Map.of(
        "easeInOutQuint", "cubic-bezier(0.86, 0, 0.07, 1)",
        "easeInOutCubic", "cubic-bezier(0.65, 0, 0.35, 1)"
    );

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
        } else if (command instanceof AnimateBehaviorCommand behaviorCommand) {
            Double customDuration = behaviorCommand.params().duration();
            return customDuration != null ? customDuration : ANIMATE_BEHAVIOR_DURATION;
        } else if (command instanceof FocusOnCommand) {
            return FOCUS_ON_DURATION;
        }
        return 1.0;
    }

    private double calculateStaggerOffset(TimelineContext context) {
        int index = context.getElementIndex();
        return Math.sqrt(index) * STAGGER_DELAY;
    }

    private String getEasing(Command command) {
        String easingName;
        if (command instanceof ConnectEntitiesCommand) {
            easingName = "easeInOutCubic";
        } else {
            easingName = "easeInOutQuint";
        }
        return EASING_MAP.get(easingName);
    }
}

