package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.Coordinate;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ConnectEntitiesFactory implements CommandFactory {

    private static final Logger log = LoggerFactory.getLogger(ConnectEntitiesFactory.class);
    private static final double ARROW_DURATION = 1.5;
    private static final double LABEL_APPEAR_OFFSET = 0.5;
    private static final String ARROW_SUFFIX = "_arrow";
    private static final String LABEL_SUFFIX = "_label";

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        ConnectEntitiesCommand connectCommand = (ConnectEntitiesCommand) command;
        List<TimelineEvent> events = new ArrayList<>();
        double arrowStartTime = sceneState.getCurrentTime();

        Optional<SceneEntity> fromEntityOpt = sceneState.getEntityById(connectCommand.params().from());
        Optional<SceneEntity> toEntityOpt = sceneState.getEntityById(connectCommand.params().to());

        if (fromEntityOpt.isEmpty()) {
            String errorMsg = String.format(
                "Cannot connect entities: source entity '%s' not found in scene", 
                connectCommand.params().from()
            );
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        if (toEntityOpt.isEmpty()) {
            String errorMsg = String.format(
                "Cannot connect entities: target entity '%s' not found in scene", 
                connectCommand.params().to()
            );
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        SceneEntity fromEntity = fromEntityOpt.get();
        SceneEntity toEntity = toEntityOpt.get();

        Coordinate fromPoint = new Coordinate(
            fromEntity.x() + fromEntity.width() / 2, 
            fromEntity.y() + fromEntity.height() / 2
        );
        Coordinate toPoint = new Coordinate(
            toEntity.x() + toEntity.width() / 2, 
            toEntity.y() + toEntity.height() / 2
        );

        events.add(TimelineEvent.builder()
            .elementId(connectCommand.id() + ARROW_SUFFIX)
            .type("arrow")
            .action("animate")
            .time(arrowStartTime)
            .duration(ARROW_DURATION)
            .from(fromPoint)
            .to(toPoint)
            .build()
        );

        if (connectCommand.params().label() != null && !connectCommand.params().label().isBlank()) {
            events.add(TimelineEvent.builder()
                .elementId(connectCommand.id() + LABEL_SUFFIX)
                .type("text")
                .action("appear")
                .time(arrowStartTime + LABEL_APPEAR_OFFSET)
                .content(connectCommand.params().label())
                .build()
            );
        }

        sceneState.advanceTime(ARROW_DURATION);

        return events;
    }

    @Override
    public boolean supports(Command command) {
        return command instanceof ConnectEntitiesCommand;
    }
}
