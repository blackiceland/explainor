package com.dev.explainor.conductor.service;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.ConnectEntitiesCommand;
import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.Storyboard;
import com.dev.explainor.conductor.factory.CommandFactory;
import com.dev.explainor.conductor.validation.StoryboardValidator;
import com.dev.explainor.renderer.domain.CameraEvent;
import com.dev.explainor.renderer.domain.Canvas;
import com.dev.explainor.renderer.domain.FinalTimeline;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    private static final Logger log = LoggerFactory.getLogger(ConductorService.class);
    private static final double DEFAULT_CANVAS_WIDTH = 1280;
    private static final double DEFAULT_CANVAS_HEIGHT = 720;
    private static final String DEFAULT_BACKGROUND_COLOR = "#DDDDDD";

    private final List<CommandFactory> factories;
    private final StoryboardValidator validator;

    public ConductorService(List<CommandFactory> factories, StoryboardValidator validator) {
        this.factories = factories;
        this.validator = validator;
    }

    /**
     * Generates a low-level animation timeline from a high-level storyboard.
     *
     * @param storyboard the high-level commands from the LLM translator
     * @return FinalTimeline ready for rendering
     * @throws NullPointerException if storyboard is null
     * @throws IllegalArgumentException if storyboard is invalid
     * @throws IllegalStateException if no factory supports a command
     */
    public FinalTimeline generateTimeline(Storyboard storyboard) {
        Objects.requireNonNull(storyboard, "Storyboard cannot be null");

        validator.validate(storyboard);

        log.info("Processing storyboard with {} commands", storyboard.commands().size());

        Graph<String, DefaultEdge> connectionsGraph = buildConnectionsGraph(storyboard);
        SceneState sceneState = new SceneState(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT, connectionsGraph);
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

        List<TimelineEvent> timelineEvents = allEvents.stream()
            .filter(e -> !"camera".equals(e.type()))
            .collect(Collectors.toList());

        List<CameraEvent> cameraEvents = allEvents.stream()
            .filter(e -> "camera".equals(e.type()))
            .map(e -> new CameraEvent(
                "pan",
                e.time(),
                e.duration() != null ? e.duration() : 0.0,
                new CameraEvent.CameraTarget(
                    e.props() != null ? (Double) e.props().get("x") : null,
                    e.props() != null ? (Double) e.props().get("y") : null,
                    e.props() != null ? (Double) e.props().get("scale") : null
                )
            ))
            .collect(Collectors.toList());

        Canvas canvas = new Canvas((int) DEFAULT_CANVAS_WIDTH, (int) DEFAULT_CANVAS_HEIGHT, DEFAULT_BACKGROUND_COLOR);
        double totalDuration = sceneState.getCurrentTime();

        log.info("Generated timeline with {} events, {} camera events, duration: {}s", 
            timelineEvents.size(), cameraEvents.size(), totalDuration);

        return new FinalTimeline(canvas, totalDuration, timelineEvents, cameraEvents);
    }

    private Graph<String, DefaultEdge> buildConnectionsGraph(Storyboard storyboard) {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        storyboard.commands().stream()
                .filter(cmd -> cmd instanceof CreateEntityCommand)
                .map(cmd -> ((CreateEntityCommand) cmd).id())
                .forEach(graph::addVertex);

        storyboard.commands().stream()
                .filter(cmd -> cmd instanceof ConnectEntitiesCommand)
                .map(cmd -> (ConnectEntitiesCommand) cmd)
                .forEach(cmd -> {
                    String from = cmd.params().from();
                    String to = cmd.params().to();
                    if (graph.containsVertex(from) && graph.containsVertex(to)) {
                        graph.addEdge(from, to);
                    }
                });

        log.debug("Built connections graph with {} vertices and {} edges",
                graph.vertexSet().size(), graph.edgeSet().size());

        return graph;
    }
}
