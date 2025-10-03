package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.service.SceneState;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SimpleHintLayoutManager implements LayoutManager {

    private static final double PADDING_HORIZONTAL_PERCENT = 0.25;
    private static final double PADDING_VERTICAL_PERCENT = 0.5;

    @Override
    public Point calculatePosition(Command command, SceneState sceneState) {
        if (command instanceof CreateEntityCommand createEntityCommand) {
            String hint = createEntityCommand.params().positionHint();
            double canvasWidth = sceneState.getCanvasWidth();
            double canvasHeight = sceneState.getCanvasHeight();

            double defaultY = canvasHeight * PADDING_VERTICAL_PERCENT;

            if (Objects.equals(hint, "left")) {
                return new Point(canvasWidth * PADDING_HORIZONTAL_PERCENT, defaultY);
            }

            if (Objects.equals(hint, "right")) {
                return new Point(canvasWidth * (1 - PADDING_HORIZONTAL_PERCENT), defaultY);
            }

            if (Objects.equals(hint, "center")) {
                return new Point(canvasWidth * 0.5, defaultY);
            }
        }

        // Default position if no hint or unsupported command
        return new Point(sceneState.getCanvasWidth() * 0.5, sceneState.getCanvasHeight() * 0.5);
    }
}
