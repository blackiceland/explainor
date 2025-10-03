package com.dev.explainor.conductor.domain;

public record FocusOnCommand(String id, FocusOnParams params) implements Command {
}
