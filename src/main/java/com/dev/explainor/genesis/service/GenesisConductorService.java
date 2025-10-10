package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.FinalTimelineV1;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.layout.model.*;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenesisConductorService {

    private final StoryboardValidator storyboardValidator;
    private final LayoutModelFactory layoutModelFactory;
    private final LayoutManager layoutManager;
    private final PathFinder pathFinder;
    private final TimelineEnricher timelineEnricher;
    private final TimelineFactory timelineFactory;


    public GenesisConductorService(
            StoryboardValidator storyboardValidator,
            LayoutModelFactory layoutModelFactory, LayoutManager layoutManager,
            PathFinder pathFinder,
            TimelineEnricher timelineEnricher,
            TimelineFactory timelineFactory) {
        this.storyboardValidator = storyboardValidator;
        this.layoutModelFactory = layoutModelFactory;
        this.layoutManager = layoutManager;
        this.pathFinder = pathFinder;
        this.timelineEnricher = timelineEnricher;
        this.timelineFactory = timelineFactory;
    }

    public FinalTimelineV1 choreograph(StoryboardV1 storyboard) {
        storyboardValidator.validate(storyboard);

        LayoutModelFactory.LayoutModelResult layoutModelResult = layoutModelFactory.createLayoutModel(storyboard);
        List<LayoutNode> layoutNodes = layoutModelResult.nodes();
        List<LayoutEdge> layoutEdges = layoutModelResult.edges();

        LayoutConstraints constraints = new LayoutConstraints(1280, 720);
        List<PositionedNode> positionedNodes = layoutManager.layout(layoutNodes, layoutEdges, constraints);

        List<RoutedEdge> routedEdges = pathFinder.routeEdges(layoutEdges, positionedNodes, constraints);
        LayoutResult layoutResult = new LayoutResult(positionedNodes, routedEdges);

        List<AnimationTrack> animationTracks = timelineEnricher.enrichWithAnimations(storyboard, layoutResult);

        return timelineFactory.createFrom(layoutResult, animationTracks);
    }
}

