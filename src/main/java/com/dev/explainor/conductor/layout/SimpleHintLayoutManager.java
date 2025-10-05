package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.service.SceneState;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SimpleHintLayoutManager implements LayoutManager {

    private static final double MARGIN_PERCENT = 0.15;
    private static final double CENTER_VERTICAL = 0.5;

    @Override
    public Point calculatePosition(Command command, SceneState sceneState) {
        if (!(command instanceof CreateEntityCommand createEntityCommand)) {
            return new Point(sceneState.getCanvasWidth() * 0.5, sceneState.getCanvasHeight() * 0.5);
        }

        String hint = Objects.requireNonNullElse(createEntityCommand.params().positionHint(), "center");
        double width = sceneState.getCanvasWidth();
        double height = sceneState.getCanvasHeight();

        return switch (hint) {
            case "left" -> new Point(width * MARGIN_PERCENT, height * CENTER_VERTICAL);
            case "right" -> new Point(width * (1 - MARGIN_PERCENT), height * CENTER_VERTICAL);
            case "center" -> new Point(width * 0.5, height * CENTER_VERTICAL);
            case "top" -> new Point(width * 0.75, height * MARGIN_PERCENT);
            case "bottom" -> new Point(width * 0.75, height * (1 - MARGIN_PERCENT));
            case "top-left" -> new Point(width * MARGIN_PERCENT, height * MARGIN_PERCENT);
            case "top-right" -> new Point(width * (1 - MARGIN_PERCENT), height * MARGIN_PERCENT);
            case "bottom-left" -> new Point(width * MARGIN_PERCENT, height * (1 - MARGIN_PERCENT));
            case "bottom-right" -> new Point(width * (1 - MARGIN_PERCENT), height * (1 - MARGIN_PERCENT));
            default -> new Point(width * 0.5, height * CENTER_VERTICAL);
        };
    }
}
