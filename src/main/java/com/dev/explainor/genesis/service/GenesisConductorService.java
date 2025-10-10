package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.layout.model.*;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GenesisConductorService {

    private static final Logger log = LoggerFactory.getLogger(GenesisConductorService.class);

    private final LayoutManager layoutManager;
    private final PathFinder pathFinder;
    private final StoryboardValidator validator;
    private final LayoutModelFactory layoutModelFactory;
    private final TimelineFactory timelineFactory;
    private final TimelineEnricher timelineEnricher;

    public GenesisConductorService(
            LayoutManager layoutManager,
            PathFinder pathFinder,
            StoryboardValidator validator,
            LayoutModelFactory layoutModelFactory,
            TimelineFactory timelineFactory,
            TimelineEnricher timelineEnricher) {
        this.layoutManager = layoutManager;
        this.pathFinder = pathFinder;
        this.validator = validator;
        this.layoutModelFactory = layoutModelFactory;
        this.timelineFactory = timelineFactory;
        this.timelineEnricher = timelineEnricher;
    }

    public FinalTimelineV1 choreograph(StoryboardV1 storyboard) {
        Objects.requireNonNull(storyboard, "Storyboard cannot be null");
        validateStoryboardVersion(storyboard);
        validateStoryboardIntegrity(storyboard);

        log.info("Processing storyboard v{} with {} commands",
            storyboard.version(), storyboard.commands().size());

        LayoutModelFactory.ExtractionResult extractionResult = layoutModelFactory.createFrom(storyboard);
        List<LayoutNode> layoutNodes = extractionResult.nodes();
        List<LayoutEdge> layoutEdges = extractionResult.edges();

        LayoutConstraints constraints = LayoutConstraints.create(1280, 720);

        List<PositionedNode> positionedNodes = layoutManager.layout(
            layoutNodes,
            layoutEdges,
            constraints
        );

        List<RoutedEdge> routedEdges = pathFinder.routeEdges(layoutEdges, positionedNodes, constraints);
        LayoutResult layoutResult = new LayoutResult(positionedNodes, routedEdges);

        List<AnimationTrack> animationTracks = timelineEnricher.enrichWithAnimations(storyboard, layoutResult);

        log.info("Generated timeline with {} nodes, {} edges and {} animation tracks", 
            layoutResult.nodes().size(), layoutResult.edges().size(), animationTracks.size());
        
        return timelineFactory.createFrom(layoutResult, animationTracks);
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
}

