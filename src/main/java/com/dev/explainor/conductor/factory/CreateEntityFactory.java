package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.Point;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CreateEntityFactory implements CommandFactory {

    private static final double DEFAULT_ENTITY_WIDTH = 200;
    private static final double DEFAULT_ENTITY_HEIGHT = 120;
    private static final double APPEAR_DURATION = 1.0;

    private final LayoutManager layoutManager;

    public CreateEntityFactory(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        CreateEntityCommand createEntityCommand = (CreateEntityCommand) command;
        List<TimelineEvent> events = new ArrayList<>();
        double startTime = sceneState.getCurrentTime();

        Point position = layoutManager.calculatePosition(createEntityCommand, sceneState);

        SceneEntity sceneEntity = new SceneEntity(
            createEntityCommand.id(),
            position.x(),
            position.y(),
            DEFAULT_ENTITY_WIDTH,
            DEFAULT_ENTITY_HEIGHT
        );
        sceneState.registerEntity(sceneEntity);

        String groupId = sceneEntity.id() + "_group";
        String shapeId = sceneEntity.id() + "_shape";
        String textId = sceneEntity.id() + "_text";

        events.add(TimelineEvent.builder()
            .elementId(groupId)
            .type("group")
            .action("appear")
            .time(startTime)
            .props(Map.of(
                "x", sceneEntity.x(),
                "y", sceneEntity.y(),
                "width", sceneEntity.width(),
                "height", sceneEntity.height()
            ))
            .children(List.of(shapeId, textId))
            .build()
        );

        events.add(TimelineEvent.builder()
            .elementId(shapeId)
            .type("shape")
            .action("appear")
            .time(startTime)
            .props(Map.of(
                "shapeType", "rectangle",
                "width", sceneEntity.width(),
                "height", sceneEntity.height(),
                "fillColor", "#FFFFFF",
                "strokeColor", "#E0E0E0",
                "strokeWidth", 1,
                "radius", 16
            ))
            .build()
        );

        events.add(TimelineEvent.builder()
            .elementId(textId)
            .type("text")
            .action("appear")
            .time(startTime)
            .content(createEntityCommand.params().label())
            .build()
        );

        sceneState.advanceTime(APPEAR_DURATION);

        return events;
    }

    @Override
    public boolean supports(Command command) {
        return command instanceof CreateEntityCommand;
    }
}
