package com.dev.explainor.genesis.dto;

public record PauseCommand(
    String id,
    PauseParams params
) implements StoryboardCommand {
}

