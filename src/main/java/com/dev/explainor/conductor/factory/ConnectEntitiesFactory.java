package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.SceneEntity;
import com.dev.explainor.conductor.layout.ArrowRoutingHelper;
import com.dev.explainor.conductor.layout.PathFinder;
import com.dev.explainor.conductor.layout.Point;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.Coordinate;
import com.dev.explainor.renderer.domain.TimelineEvent;
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

        Point fromCenter = new Point(
            fromEntity.x() + fromEntity.width() / 2,
            fromEntity.y() + fromEntity.height() / 2
        );
        Point toCenter = new Point(
            toEntity.x() + toEntity.width() / 2,
            toEntity.y() + toEntity.height() / 2
        );

        Point fromEdge = ArrowRoutingHelper.calculateEdgePoint(
            fromCenter, 
            toCenter, 
            fromEntity.width(), 
            fromEntity.height()
        );
        Point toEdge = ArrowRoutingHelper.calculateEdgePoint(
            toCenter, 
            fromCenter, 
            toEntity.width(), 
            toEntity.height()
        );

        Collection<SceneEntity> obstacles = sceneState.getEntities().values().stream()
                .filter(e -> !e.id().equals(fromEntity.id()) && !e.id().equals(toEntity.id()))
                .collect(Collectors.toList());

        List<Point> pathPoints = PathFinder.findOrthogonalPath(
                fromEdge,
                toEdge,
                obstacles,
                sceneState.getCanvasWidth(),
                sceneState.getCanvasHeight()
        );

        List<Coordinate> path = pathPoints.stream()
                .map(p -> new Coordinate(p.x(), p.y()))
                .collect(Collectors.toList());

        Coordinate fromPoint = path.get(0);
        Coordinate toPoint = path.get(path.size() - 1);

        events.add(TimelineEvent.builder()
            .elementId(connectCommand.id() + ARROW_SUFFIX)
            .type("arrow")
            .action("animate")
            .time(arrowStartTime)
            .duration(ARROW_DURATION)
            .from(fromPoint)
            .to(toPoint)
            .path(path.size() > 2 ? path : null)
            .build()
        );

        if (connectCommand.params().label() != null && !connectCommand.params().label().isBlank()) {
            double midX = (fromPoint.x() + toPoint.x()) / 2;
            double midY = (fromPoint.y() + toPoint.y()) / 2;
            
            double dx = toPoint.x() - fromPoint.x();
            double dy = toPoint.y() - fromPoint.y();
            double perpX = -dy;
            double perpY = dx;
            double length = Math.sqrt(perpX * perpX + perpY * perpY);
            
            if (length > 0) {
                perpX = perpX / length * 40;
                perpY = perpY / length * 40;
            }
            
            double labelX = midX + perpX;
            double labelY = midY + perpY;

            events.add(TimelineEvent.builder()
                .elementId(connectCommand.id() + LABEL_SUFFIX)
                .type("text")
                .action("appear")
                .time(arrowStartTime + LABEL_APPEAR_OFFSET)
                .content(connectCommand.params().label())
                .props(Map.of(
                    "x", labelX,
                    "y", labelY,
                    "fontSize", 14
                ))
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
