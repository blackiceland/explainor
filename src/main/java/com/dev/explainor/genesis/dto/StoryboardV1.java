package com.dev.explainor.genesis.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StoryboardV1(
    @NotBlank String version,
    @NotNull @Valid List<StoryboardCommand> commands
) {
    public static final String CURRENT_VERSION = "1.0.0";

    public static StoryboardV1 create(List<StoryboardCommand> commands) {
        return new StoryboardV1(CURRENT_VERSION, commands);
    }
}

