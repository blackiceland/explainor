package com.dev.explainor.conductor.domain;

public record ConnectEntitiesCommand(String id, ConnectEntitiesParams params) implements Command {
}
