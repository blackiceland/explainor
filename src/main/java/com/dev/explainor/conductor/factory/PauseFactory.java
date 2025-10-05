package com.dev.explainor.conductor.factory;

import com.dev.explainor.conductor.domain.Command;
import com.dev.explainor.conductor.domain.PauseCommand;
import com.dev.explainor.conductor.service.SceneState;
import com.dev.explainor.renderer.domain.TimelineEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PauseFactory implements CommandFactory {

    @Override
    public boolean supports(Command command) {
        return command instanceof PauseCommand;
    }

    @Override
    public List<TimelineEvent> createTimelineEvents(Command command, SceneState sceneState) {
        PauseCommand pauseCommand = (PauseCommand) command;
        double duration = pauseCommand.params().duration();

        sceneState.advanceTime(duration);

        return Collections.emptyList();
    }
}

