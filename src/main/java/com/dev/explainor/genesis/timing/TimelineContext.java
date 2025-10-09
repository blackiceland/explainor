package com.dev.explainor.genesis.timing;

public class TimelineContext {
    private double currentTime;
    private int elementIndex;

    private TimelineContext(double currentTime, int elementIndex) {
        this.currentTime = currentTime;
        this.elementIndex = elementIndex;
    }

    public static TimelineContext initial() {
        return new TimelineContext(0.0, 0);
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public int getElementIndex() {
        return elementIndex;
    }

    public void advance(double duration) {
        this.currentTime += duration;
    }

    public void incrementIndex() {
        this.elementIndex++;
    }

    public void reset() {
        this.currentTime = 0.0;
        this.elementIndex = 0;
    }
}

