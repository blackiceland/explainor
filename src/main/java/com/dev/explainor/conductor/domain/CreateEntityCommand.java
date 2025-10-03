package com.dev.explainor.conductor.domain;

public record CreateEntityCommand(String id, CreateEntityParams params) implements Command {
}
