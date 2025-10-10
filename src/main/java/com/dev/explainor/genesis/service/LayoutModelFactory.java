package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.*;
import com.dev.explainor.genesis.dto.StoryboardV1;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.LayoutNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LayoutModelFactory {

    public LayoutModelResult createLayoutModel(StoryboardV1 storyboard) {
        Set<LayoutNode> nodes = new HashSet<>();
        List<LayoutEdge> edges = new ArrayList<>();

        for (Command command : storyboard.commands()) {
            switch (command) {
                case CreateEntityCommand createCmd -> {
                    CreateEntityParams params = createCmd.params();
                    LayoutNode node = new LayoutNode(createCmd.id(), params.label(), params.icon(), params.positionHint());
                    nodes.add(node);
                }
                case ConnectEntitiesCommand connectCmd -> {
                    ConnectEntitiesParams params = connectCmd.params();
                    LayoutEdge edge = new LayoutEdge(connectCmd.id(), params.from(), params.to(), params.label(), params.lineStyle());
                    edges.add(edge);
                }
                case AnimateBehaviorCommand behaviorCmd -> {}
                case FocusOnCommand focusCmd -> {}
                case PauseCommand pauseCmd -> {}
            }
        }
        return new LayoutModelResult(new ArrayList<>(nodes), edges);
    }

    public record LayoutModelResult(List<LayoutNode> nodes, List<LayoutEdge> edges) {}
}
