package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.FocusOnCommand;
import com.dev.explainor.genesis.dto.*;
import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.PauseCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.model.*;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GenesisConductorService {

    private static final Logger log = LoggerFactory.getLogger(GenesisConductorService.class);
    private static final int DEFAULT_CANVAS_WIDTH = 1280;
    private static final int DEFAULT_CANVAS_HEIGHT = 720;

    private final LayoutManager layoutManager;
    private final StoryboardValidator validator;

    public GenesisConductorService(
            @Qualifier("genesisLayoutManager") LayoutManager layoutManager,
            StoryboardValidator validator) {
        this.layoutManager = layoutManager;
        this.validator = validator;
    }

    public FinalTimelineV1 choreograph(StoryboardV1 storyboard) {
        Objects.requireNonNull(storyboard, "Storyboard cannot be null");
        validateStoryboardVersion(storyboard);
        validateStoryboardIntegrity(storyboard);

        log.info("Processing storyboard v{} with {} commands", 
            storyboard.version(), storyboard.commands().size());

        ExtractionResult extractionResult = extractNodesAndEdges(storyboard);
        List<LayoutNode> layoutNodes = extractionResult.nodes();
        List<LayoutEdge> layoutEdges = extractionResult.edges();

        LayoutConstraints constraints = LayoutConstraints.create(
            DEFAULT_CANVAS_WIDTH, 
            DEFAULT_CANVAS_HEIGHT
        );

        LayoutResult layoutResult = layoutManager.layout(
            layoutNodes, 
            layoutEdges, 
            constraints
        );

        Stage stage = new Stage(
            DEFAULT_CANVAS_WIDTH,
            DEFAULT_CANVAS_HEIGHT
        );

        List<TimelineNode> nodes = convertToNodes(layoutResult.nodes());
        List<TimelineEdge> edges = convertToEdges(layoutResult.edges());

        log.info("Generated timeline with {} nodes and {} edges", nodes.size(), edges.size());

        return FinalTimelineV1.create(stage, nodes, edges);
    }

    private void validateStoryboardVersion(StoryboardV1 storyboard) {
        if (!StoryboardV1.CURRENT_VERSION.equals(storyboard.version())) {
            throw new IllegalArgumentException(
                "Unsupported storyboard version: " + storyboard.version() + 
                ". Expected: " + StoryboardV1.CURRENT_VERSION
            );
        }
    }

    private void validateStoryboardIntegrity(StoryboardV1 storyboard) {
        StoryboardValidator.ValidationResult result = validator.validate(storyboard);
        
        if (!result.valid()) {
            throw new IllegalArgumentException(
                "Invalid storyboard: " + result.errorMessage()
            );
        }
    }

    private ExtractionResult extractNodesAndEdges(StoryboardV1 storyboard) {
        List<LayoutNode> nodes = new ArrayList<>();
        List<LayoutEdge> edges = new ArrayList<>();
        
        for (Command command : storyboard.commands()) {
            switch (command) {
                case CreateEntityCommand createCmd -> nodes.add(new LayoutNode(
                    createCmd.id(),
                    createCmd.params().label(),
                    createCmd.params().icon(),
                    createCmd.params().positionHint()
                ));
                case ConnectEntitiesCommand connectCmd -> edges.add(new LayoutEdge(
                    connectCmd.id(),
                    connectCmd.params().from(),
                    connectCmd.params().to(),
                    connectCmd.params().label()
                ));
                case PauseCommand pauseCmd -> {
                }
                case FocusOnCommand focusCmd -> {
                }
            }
        }
        
        return new ExtractionResult(nodes, edges);
    }

    private record ExtractionResult(
        List<LayoutNode> nodes,
        List<LayoutEdge> edges
    ) {}

    private List<TimelineNode> convertToNodes(List<PositionedNode> positionedNodes) {
        return positionedNodes.stream()
            .map(positionedNode -> new TimelineNode(
                positionedNode.id(),
                positionedNode.label(),
                positionedNode.icon(),
                positionedNode.x(),
                positionedNode.y()
            ))
            .toList();
    }

    private List<TimelineEdge> convertToEdges(List<RoutedEdge> routedEdges) {
        return routedEdges.stream()
            .map(routedEdge -> new TimelineEdge(
                routedEdge.id(),
                routedEdge.from(),
                routedEdge.to(),
                routedEdge.label(),
                routedEdge.path()
            ))
            .toList();
    }
}

