package com.dev.explainor.genesis.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEntityParams(
    @NotBlank String label,
    @NotBlank String icon,
    String positionHint
) {}

