package com.dev.explainor.genesis.domain;

public record CreateEntityCommand(String id, CreateEntityParams params) implements Command {
}

