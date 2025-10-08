package com.dev.explainor.helios.factory;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.helios.domain.SceneEntity;
import com.dev.explainor.helios.layout.ArrowRoutingHelper;
import com.dev.explainor.helios.layout.PathFinder;
import com.dev.explainor.helios.layout.Point;
import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.renderer.domain.Coordinate;
import com.dev.explainor.genesis.renderer.domain.TimelineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ConnectEntitiesFactory implements CommandFactory {

    private static final Logger log = LoggerFactory.getLogger(ConnectEntitiesFactory.class);
    private static final double ARROW_DURATION = 1.5;
    private static final double LABEL_APPEAR_OFFSET = 0.5;
    private static final double LABEL_OFFSET_DISTANCE = 40.0;
    private static final int LABEL_FONT_SIZE = 14;
    private static final String ARROW_SUFFIX = "_arrow";
    private static final String LABEL_SUFFIX = "_label";

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        ConnectEntitiesCommand connectCommand = (ConnectEntitiesCommand) command;
        List<TimelineEvent> events = new ArrayList<>();
        double arrowStartTime = sceneState.getCurrentTime();

        EntitiesPair entities = validateAndGetEntities(connectCommand, sceneState);
        List<Coordinate> path = calculateArrowPath(entities, sceneState);

        events.add(createArrowEvent(connectCommand.id(), path, arrowStartTime));
        createLabelEvent(connectCommand, path, arrowStartTime)
                .ifPresent(events::add);

        sceneState.advanceTime(ARROW_DURATION);

        return events;
    }

    private EntitiesPair validateAndGetEntities(ConnectEntitiesCommand command, SceneState sceneState) {
        SceneEntity fromEntity = sceneState.getEntityById(command.params().from())
                .orElseThrow(() -> {
                    String errorMsg = String.format(
                            "Cannot connect entities: source entity '%s' not found in scene",
                            command.params().from()
                    );
                    log.error(errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });

        SceneEntity toEntity = sceneState.getEntityById(command.params().to())
                .orElseThrow(() -> {
                    String errorMsg = String.format(
                            "Cannot connect entities: target entity '%s' not found in scene",
                            command.params().to()
                    );
                    log.error(errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });

        return new EntitiesPair(fromEntity, toEntity);
    }

    private List<Coordinate> calculateArrowPath(EntitiesPair entities, SceneState sceneState) {
        Point fromCenter = calculateCenter(entities.from());
        Point toCenter = calculateCenter(entities.to());

        Point fromEdge = ArrowRoutingHelper.calculateEdgePoint(
                fromCenter, toCenter,
                entities.from().width(), entities.from().height()
        );
        Point toEdge = ArrowRoutingHelper.calculateEdgePoint(
                toCenter, fromCenter,
                entities.to().width(), entities.to().height()
        );

        Collection<SceneEntity> obstacles = sceneState.getEntities().values().stream()
                .filter(e -> !e.id().equals(entities.from().id()) && !e.id().equals(entities.to().id()))
                .collect(Collectors.toList());

        List<Point> pathPoints = PathFinder.findOrthogonalPath(
                fromEdge, toEdge, obstacles,
                sceneState.getCanvasWidth(), sceneState.getCanvasHeight()
        );

        return pathPoints.stream()
                .map(p -> new Coordinate(p.x(), p.y()))
                .collect(Collectors.toList());
    }

    private Point calculateCenter(SceneEntity entity) {
        return new Point(
                entity.x() + entity.width() / 2,
                entity.y() + entity.height() / 2
        );
    }

    private TimelineEvent createArrowEvent(String commandId, List<Coordinate> path, double startTime) {
        Coordinate fromPoint = path.get(0);
        Coordinate toPoint = path.get(path.size() - 1);

        return TimelineEvent.builder()
                .elementId(commandId + ARROW_SUFFIX)
                .type("arrow")
                .action("animate")
                .time(startTime)
                .duration(ARROW_DURATION)
                .from(fromPoint)
                .to(toPoint)
                .path(path.size() > 2 ? path : null)
                .build();
    }

    private Optional<TimelineEvent> createLabelEvent(
            ConnectEntitiesCommand command,
            List<Coordinate> path,
            double startTime) {
        
        String label = command.params().label();
        if (label == null || label.isBlank()) {
            return Optional.empty();
        }

        Coordinate fromPoint = path.get(0);
        Coordinate toPoint = path.get(path.size() - 1);
        Coordinate labelPosition = calculateLabelPosition(fromPoint, toPoint);

        TimelineEvent labelEvent = TimelineEvent.builder()
                .elementId(command.id() + LABEL_SUFFIX)
                .type("text")
                .action("appear")
                .time(startTime + LABEL_APPEAR_OFFSET)
                .content(label)
                .props(Map.of(
                        "x", labelPosition.x(),
                        "y", labelPosition.y(),
                        "fontSize", LABEL_FONT_SIZE
                ))
                .build();

        return Optional.of(labelEvent);
    }

    private Coordinate calculateLabelPosition(Coordinate from, Coordinate to) {
        double midX = (from.x() + to.x()) / 2;
        double midY = (from.y() + to.y()) / 2;

        double dx = to.x() - from.x();
        double dy = to.y() - from.y();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            double perpX = -dy / length * LABEL_OFFSET_DISTANCE;
            double perpY = dx / length * LABEL_OFFSET_DISTANCE;
            return new Coordinate(midX + perpX, midY + perpY);
        }

        return new Coordinate(midX, midY);
    }

    private record EntitiesPair(SceneEntity from, SceneEntity to) {
    }

    @Override
    public boolean supports(Command command) {
        return command instanceof ConnectEntitiesCommand;
    }
}
