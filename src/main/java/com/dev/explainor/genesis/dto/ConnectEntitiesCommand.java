package com.dev.explainor.genesis.dto;

public record ConnectEntitiesCommand(String id,
    ConnectEntitiesParams params
) implements StoryboardCommand {
}

