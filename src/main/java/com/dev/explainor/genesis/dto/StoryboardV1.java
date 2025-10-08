package com.dev.explainor.genesis.dto;

import com.dev.explainor.genesis.domain.Command;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StoryboardV1(
    @NotBlank String version,
    @NotNull @Valid List<Command> commands
) {
    public static final String CURRENT_VERSION = "1.0.0";

    public static StoryboardV1 create(List<Command> commands) {
        return new StoryboardV1(CURRENT_VERSION, commands);
    }
}

