package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.FocusOnCommand;
import com.dev.explainor.genesis.domain.PauseCommand;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.LayoutNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LayoutModelFactory {

    public ExtractionResult createFrom(StoryboardV1 storyboard) {
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

    public record ExtractionResult(
        List<LayoutNode> nodes,
        List<LayoutEdge> edges
    ) {}
}
