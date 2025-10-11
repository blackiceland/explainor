package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.Command;
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
    private final TimingProvider timingProvider;
    private final AnimationTrackFactory animationTrackFactory;

    public TimelineEnricher(
            TimingProvider timingProvider, 
            AnimationTrackFactory animationTrackFactory) {
        this.timingProvider = timingProvider;
        this.animationTrackFactory = animationTrackFactory;
    }

    public List<AnimationTrack> enrichWithAnimations(StoryboardV1 storyboard, LayoutResult layoutResult) {
        List<AnimationTrack> tracks = new ArrayList<>();
        TimelineContext context = TimelineContext.initial();
        
        for (Command command : storyboard.commands()) {
            TimingInfo timing = timingProvider.calculateTiming(command, context);
            
            tracks.addAll(animationTrackFactory.createTracks(command, timing, layoutResult));
            context.advance(timing.duration());
            context.incrementIndex();
        }
        
        log.info("Generated {} animation tracks", tracks.size());
        return tracks;
    }
}
