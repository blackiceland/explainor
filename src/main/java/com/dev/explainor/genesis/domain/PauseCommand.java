package com.dev.explainor.genesis.domain;

public record PauseCommand(String id, PauseParams params) implements Command {
}

