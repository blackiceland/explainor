package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.TimelineEvent;

import java.util.List;

public interface CommandFactory {
    List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState);

    boolean supports(Command command);
}
