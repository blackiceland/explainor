package com.dev.explainor.helios.factory;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.FocusOnCommand;
import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.renderer.domain.TimelineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FocusOnFactory implements CommandFactory {

    private static final Logger log = LoggerFactory.getLogger(FocusOnFactory.class);
    private static final double DEFAULT_CAMERA_DURATION = 1.0;

    @Override
    public boolean supports(Command command) {
        return command instanceof FocusOnCommand;
    }

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        FocusOnCommand focusCommand = (FocusOnCommand) command;
        String target = focusCommand.params().target();
        Double scale = focusCommand.params().scale();

        if (scale == null) {
            scale = 1.0;
        }

        double x = sceneState.getCanvasWidth() / 2.0;
        double y = sceneState.getCanvasHeight() / 2.0;

        if (target != null && !target.equals("center")) {
            var entity = sceneState.getEntityById(target);
            if (entity.isPresent()) {
                x = entity.get().x();
                y = entity.get().y();
            } else {
                log.warn("FocusOn target entity '{}' not found, using center", target);
            }
        }

        TimelineEvent cameraEvent = TimelineEvent.builder()
                .elementId("camera")
                .type("camera")
                .action("move")
                .time(sceneState.getCurrentTime())
                .duration(DEFAULT_CAMERA_DURATION)
                .props(Map.of(
                        "x", x,
                        "y", y,
                        "scale", scale
                ))
                .build();

        sceneState.advanceTime(DEFAULT_CAMERA_DURATION);

        return List.of(cameraEvent);
    }
}

