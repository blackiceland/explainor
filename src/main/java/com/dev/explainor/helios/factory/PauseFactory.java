package com.dev.explainor.helios.factory;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.PauseCommand;
import com.dev.explainor.helios.service.SceneState;
import com.dev.explainor.genesis.renderer.domain.TimelineEvent;
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

