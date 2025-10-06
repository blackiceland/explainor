package com.dev.explainor.genesis.dto;

import jakarta.validation.constraints.Positive;

public record PauseParams(
    @Positive double duration
) {}

