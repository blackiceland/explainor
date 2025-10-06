package com.dev.explainor.genesis.dto;

import jakarta.validation.constraints.NotBlank;

public record ConnectEntitiesParams(
    @NotBlank String from,
    @NotBlank String to,
    String label
) {}

