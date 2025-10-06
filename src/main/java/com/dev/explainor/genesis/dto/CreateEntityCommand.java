package com.dev.explainor.genesis.dto;

public record CreateEntityCommand(
    String id,
    CreateEntityParams params
) implements StoryboardCommand {
}

