package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.conductor.layout.Point;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreateEntityFactory implements CommandFactory {

    private static final double DEFAULT_ENTITY_WIDTH = 200;
    private static final double DEFAULT_ENTITY_HEIGHT = 120;
    private static final double APPEAR_DURATION = 1.0;
    private static final String SHAPE_SUFFIX = "_shape";

    private final LayoutManager layoutManager;

    public CreateEntityFactory(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        CreateEntityCommand createEntityCommand = (CreateEntityCommand) command;
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

        String shapeId = sceneEntity.id() + SHAPE_SUFFIX;
        
        Map<String, Object> props = new HashMap<>();
        props.put("x", sceneEntity.x());
        props.put("y", sceneEntity.y());
        props.put("width", sceneEntity.width());
        props.put("height", sceneEntity.height());
        props.put("label", createEntityCommand.params().label());
        props.put("icon", createEntityCommand.params().icon());
        props.put("shapeType", createEntityCommand.params().shape() != null ? createEntityCommand.params().shape() : "rectangle");
        
        TimelineEvent event = TimelineEvent.builder()
            .elementId(shapeId)
            .type("shape")
            .action("appear")
            .time(startTime)
            .props(props)
            .build();

        sceneState.advanceTime(APPEAR_DURATION);

        return List.of(event);
    }

    @Override
    public boolean supports(Command command) {
        return command instanceof CreateEntityCommand;
    }
}
