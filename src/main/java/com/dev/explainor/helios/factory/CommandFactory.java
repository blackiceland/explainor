package com.dev.explainor.helios.factory;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.renderer.domain.TimelineEvent;

import java.util.List;

public interface CommandFactory {
    List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState);

    boolean supports(Command command);
}
