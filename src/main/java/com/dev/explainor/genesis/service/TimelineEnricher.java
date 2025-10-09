package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.timing.TimingProvider;
import com.dev.explainor.genesis.timing.TimingInfo;
import com.dev.explainor.genesis.timing.TimelineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimelineEnricher {
    private static final Logger log = LoggerFactory.getLogger(TimelineEnricher.class);
    
    private final TimingProvider timingProvider;

    public TimelineEnricher(TimingProvider timingProvider) {
        this.timingProvider = timingProvider;
    }

    public List<AnimationTrack> enrichWithAnimations(StoryboardV1 storyboard) {
        List<AnimationTrack> tracks = new ArrayList<>();
        TimelineContext context = TimelineContext.initial();
        
        for (Command command : storyboard.commands()) {
            TimingInfo timing = timingProvider.calculateTiming(command, context);
            
            if (command instanceof CreateEntityCommand createCmd) {
                tracks.add(createNodeAppearanceTrack(createCmd, timing));
            } else if (command instanceof ConnectEntitiesCommand connectCmd) {
                tracks.add(createEdgeAppearanceTrack(connectCmd, timing));
            }
            
            context.advance(timing.duration());
            context.incrementIndex();
        }
        
        log.info("Generated {} animation tracks", tracks.size());
        return tracks;
    }

    private AnimationTrack createNodeAppearanceTrack(CreateEntityCommand command, TimingInfo timing) {
        String targetId = command.id();
        List<AnimationSegment> segments = List.of(
            AnimationSegment.opacity(timing.startTime(), timing.endTime(), timing.easing()),
            AnimationSegment.scale(timing.startTime(), timing.endTime(), timing.easing())
        );
        return AnimationTrack.nodeTrack(targetId, segments);
    }

    private AnimationTrack createEdgeAppearanceTrack(ConnectEntitiesCommand command, TimingInfo timing) {
        String targetId = command.params().from() + "-" + command.params().to();
        List<AnimationSegment> segments = List.of(
            AnimationSegment.opacity(timing.startTime(), timing.endTime(), timing.easing())
        );
        return AnimationTrack.edgeTrack(targetId, segments);
    }
}

