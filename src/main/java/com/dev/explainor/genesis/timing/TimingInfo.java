package com.dev.explainor.genesis.timing;

public record TimingInfo(
    double startTime,
    double duration,
    String easing
) {
    public static TimingInfo of(double startTime, double duration, String easing) {
        return new TimingInfo(startTime, duration, easing);
    }

    public static TimingInfo of(double startTime, double duration) {
        return new TimingInfo(startTime, duration, "easeInOutQuint");
    }

    public double endTime() {
        return startTime + duration;
    }
}

