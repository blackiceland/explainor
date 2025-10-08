package com.dev.explainor.genesis.domain;

public record ConnectEntitiesCommand(String id, ConnectEntitiesParams params) implements Command {
}

