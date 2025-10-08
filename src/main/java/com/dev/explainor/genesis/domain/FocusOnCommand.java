package com.dev.explainor.genesis.domain;

public record FocusOnCommand(String id, FocusOnParams params) implements Command {
}

