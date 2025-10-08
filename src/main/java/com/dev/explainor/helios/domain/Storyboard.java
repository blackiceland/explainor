package com.dev.explainor.helios.domain;

import com.dev.explainor.genesis.domain.Command;

import java.util.List;

public record Storyboard(List<Command> commands) {
}
