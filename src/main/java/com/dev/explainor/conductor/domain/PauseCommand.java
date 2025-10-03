package com.dev.explainor.conductor.domain;

public record PauseCommand(String id, PauseParams params) implements Command {
}
