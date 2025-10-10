package com.dev.explainor.genesis.domain;

public record AnimateBehaviorCommand(String id, AnimateBehaviorParams params) implements Command {
}

