package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.*;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.model.LayoutResult;
import com.dev.explainor.genesis.timing.TimelineContext;
import com.dev.explainor.genesis.timing.TimingInfo;
import com.dev.explainor.genesis.timing.TimingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimelineEnricher {
    private static final Logger log = LoggerFactory.getLogger(TimelineEnricher.class);
    private static final Double DEFAULT_FOCUS_DURATION = 1.5;
    
    private final TimingProvider timingProvider;
    private final BehaviorFactory behaviorFactory;
    private final CameraOrchestrator cameraOrchestrator;

    public TimelineEnricher(
            TimingProvider timingProvider, 
            BehaviorFactory behaviorFactory,
            CameraOrchestrator cameraOrchestrator) {
        this.timingProvider = timingProvider;
        this.behaviorFactory = behaviorFactory;
        this.cameraOrchestrator = cameraOrchestrator;
    }

    public List<AnimationTrack> enrichWithAnimations(StoryboardV1 storyboard, LayoutResult layoutResult) {
        List<AnimationTrack> tracks = new ArrayList<>();
        TimelineContext context = TimelineContext.initial();
        
        for (Command command : storyboard.commands()) {
            TimingInfo timing = timingProvider.calculateTiming(command, context);
            
            switch (command) {
                case CreateEntityCommand createCmd -> 
                    tracks.add(createNodeAppearanceTrack(createCmd, timing));
                    
                case ConnectEntitiesCommand connectCmd -> {
                    tracks.add(createEdgeAppearanceTrack(connectCmd, timing));
                    tracks.add(createArrowTrack(connectCmd, timing));
                }
                    
                case AnimateBehaviorCommand behaviorCmd -> 
                    tracks.addAll(behaviorFactory.createBehaviorTracks(
                        behaviorCmd.params(),
                        behaviorCmd.id(),
                        timing.startTime(),
                        layoutResult.nodes(),
                        layoutResult.edges()
                    ));
                    
                case FocusOnCommand focusCmd -> {
                    FocusOnParams params = focusCmd.params();
                    tracks.add(cameraOrchestrator.createFocusTrack(
                        params.target(),
                        timing.startTime(),
                        DEFAULT_FOCUS_DURATION,
                        params.scale(),
                        layoutResult.nodes()
                    ));
                }
                    
                default -> {}
            }
            
            context.advance(timing.duration());
            context.incrementIndex();
        }
        
        log.info("Generated {} animation tracks", tracks.size());
        return tracks;
    }

    private AnimationTrack createNodeAppearanceTrack(CreateEntityCommand command, TimingInfo timing) {
        String targetId = command.id();
        List<AnimationSegment> segments = new ArrayList<>();
        
        segments.add(AnimationSegment.opacity(timing.startTime(), timing.endTime(), timing.easing()));
        segments.add(AnimationSegment.scale(timing.startTime(), timing.endTime(), timing.easing()));
        
        addBreathingSegments(segments, timing.endTime());
        
        return AnimationTrack.nodeTrack(targetId, segments);
    }
    
    private void addBreathingSegments(List<AnimationSegment> segments, double startTime) {
        double breathCycleDuration = 4.5;
        int breathCycles = 3;
        
        for (int i = 0; i < breathCycles; i++) {
            double cycleStart = startTime + i * breathCycleDuration;
            double cycleMid = cycleStart + breathCycleDuration / 2;
            double cycleEnd = cycleStart + breathCycleDuration;
            
            segments.add(AnimationSegment.scale(cycleStart, cycleMid, "easeInOutQuad", 1.0, 1.02));
            segments.add(AnimationSegment.scale(cycleMid, cycleEnd, "easeInOutQuad", 1.02, 1.0));
        }
    }

    private AnimationTrack createEdgeAppearanceTrack(ConnectEntitiesCommand command, TimingInfo timing) {
        String targetId = command.id();
        List<AnimationSegment> segments = List.of(
            AnimationSegment.opacity(timing.startTime(), timing.endTime(), timing.easing())
        );
        return AnimationTrack.edgeTrack(targetId, segments);
    }
    
    private AnimationTrack createArrowTrack(ConnectEntitiesCommand command, TimingInfo timing) {
        String edgeId = command.params().from() + "-" + command.params().to();
        double duration = timing.duration();
        double arrowDelay = timing.startTime() + duration * 0.7;
        double arrowDuration = duration * 0.3;
        
        List<AnimationSegment> segments = List.of(
            AnimationSegment.opacity(arrowDelay, arrowDelay + arrowDuration, timing.easing())
        );
        return AnimationTrack.arrowTrack(edgeId, segments);
    }
}
