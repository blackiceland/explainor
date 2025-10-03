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

@Service
public class ConductorService {

    private final LayoutManager layoutManager;
    private final List<CommandFactory> factories;

    public ConductorService(LayoutManager layoutManager, List<CommandFactory> factories) {
        this.layoutManager = layoutManager;
        this.factories = factories;
    }

    public FinalTimeline generateTimeline(Storyboard storyboard) {
        final double DEFAULT_CANVAS_WIDTH = 1280;
        final double DEFAULT_CANVAS_HEIGHT = 720;
        final String DEFAULT_BACKGROUND_COLOR = "#DDDDDD";

        SceneState sceneState = new SceneState(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
        List<TimelineEvent> allEvents = new ArrayList<>();

        for (Command command : storyboard.commands()) {
            factories.stream()
                .filter(factory -> factory.supports(command))
                .findFirst()
                .ifPresent(factory -> {
                    List<TimelineEvent> events = factory.createTimelineEvents(command, sceneState);
                    allEvents.addAll(events);
                });
        }

        Canvas canvas = new Canvas((int) DEFAULT_CANVAS_WIDTH, (int) DEFAULT_CANVAS_HEIGHT, DEFAULT_BACKGROUND_COLOR);
        double totalDuration = sceneState.getCurrentTime();

        return new FinalTimeline(canvas, totalDuration, allEvents);
    }
}
