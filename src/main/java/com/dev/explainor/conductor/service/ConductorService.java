package com.dev.explainor.conductor.service;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.Storyboard;
import com.dev.explainor.conductor.factory.CommandFactory;
import com.dev.explainor.conductor.layout.LayoutManager;
import com.dev.explainor.renderer.domain.Canvas;
import com.dev.explainor.renderer.domain.FinalTimeline;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ConductorService {

    private static final double DEFAULT_CANVAS_WIDTH = 1280;
    private static final double DEFAULT_CANVAS_HEIGHT = 720;
    private static final String DEFAULT_BACKGROUND_COLOR = "#DDDDDD";

    private final LayoutManager layoutManager;
    private final List<CommandFactory> factories;

    public ConductorService(LayoutManager layoutManager, List<CommandFactory> factories) {
        this.layoutManager = layoutManager;
        this.factories = factories;
    }

    /**
     * Generates a low-level animation timeline from a high-level storyboard.
     *
     * @param storyboard the high-level commands from the LLM translator
     * @return FinalTimeline ready for rendering
     * @throws NullPointerException if storyboard is null
     * @throws IllegalStateException if no factory supports a command
     */
    public FinalTimeline generateTimeline(Storyboard storyboard) {
        Objects.requireNonNull(storyboard, "Storyboard cannot be null");

        SceneState sceneState = new SceneState(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
        List<TimelineEvent> allEvents = new ArrayList<>();

        for (Command command : storyboard.commands()) {
            CommandFactory factory = factories.stream()
                .filter(f -> f.supports(command))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                    "No factory found for command type: " + command.getClass().getSimpleName()
                ));

            List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);
            allEvents.addAll(events);
        }

        Canvas canvas = new Canvas((int) DEFAULT_CANVAS_WIDTH, (int) DEFAULT_CANVAS_HEIGHT, DEFAULT_BACKGROUND_COLOR);
        double totalDuration = sceneState.getCurrentTime();

        return new FinalTimeline(canvas, totalDuration, allEvents);
    }
}
