package com.dev.explainor.genesis.timing;

import com.dev.explainor.genesis.domain.Command;

public interface TimingProvider {
    TimingInfo calculateTiming(Command command, TimelineContext context);
}

