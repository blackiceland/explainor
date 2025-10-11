package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.AnimateBehaviorCommand;
import com.dev.explainor.genesis.domain.AnimateBehaviorParams;
import com.dev.explainor.genesis.domain.Command;
import com.dev.explainor.genesis.domain.ConnectEntitiesCommand;
import com.dev.explainor.genesis.domain.ConnectEntitiesParams;
import com.dev.explainor.genesis.domain.CreateEntityCommand;
import com.dev.explainor.genesis.domain.FocusOnCommand;
import com.dev.explainor.genesis.domain.FocusOnParams;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.LayoutResult;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import com.dev.explainor.genesis.timing.TimingInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AnimationTrackFactory {

    private static final double DEFAULT_FLOW_SPEED = 200.0;
    private static final double DEFAULT_ORBIT_DURATION = 3.0;
    private static final double DEFAULT_ORBIT_RADIUS = 80.0;
    private static final double DEFAULT_FOCUS_DURATION = 1.5;
    private static final double DEFAULT_FOCUS_SCALE = 1.5;
    private static final double DEFAULT_FOCUS_ZOOM = 1.0;
    private static final double DEFAULT_FOCUS_SPEED = 1.0;
    private static final double DEFAULT_ARROW_DELAY_RATIO = 0.7;
    private static final double PARTICLE_FADE_DURATION = 0.2;

    public List<AnimationTrack> createTracks(Command command, TimingInfo timing, LayoutResult layoutResult) {
        return switch (command) {
            case CreateEntityCommand createCommand -> List.of(createNodeAppearanceTrack(createCommand, timing));
            case ConnectEntitiesCommand connectCommand -> createEdgeTracks(connectCommand, timing);
            case AnimateBehaviorCommand animateCommand -> createBehaviorTracks(animateCommand, timing, layoutResult);
            case FocusOnCommand focusCommand -> List.of(createFocusTrack(focusCommand, timing, layoutResult.nodes()));
            default -> List.of();
        };
    }

    private AnimationTrack createNodeAppearanceTrack(CreateEntityCommand command, TimingInfo timing) {
        List<AnimationSegment> segments = new ArrayList<>();
        segments.add(AnimationSegment.opacity(timing.startTime(), timing.endTime(), resolveEasing(timing.easing())));
        segments.add(AnimationSegment.scale(timing.startTime(), timing.endTime(), resolveEasing(timing.easing())));
        addBreathingSegments(segments, timing.endTime());
        return AnimationTrack.nodeTrack(command.id(), segments);
    }

    private List<AnimationTrack> createEdgeTracks(ConnectEntitiesCommand command, TimingInfo timing) {
        List<AnimationTrack> tracks = new ArrayList<>();
        tracks.add(createEdgeAppearanceTrack(command, timing));
        arrowTrack(command.params(), timing).ifPresent(tracks::add);
        return tracks;
    }

    private AnimationTrack createEdgeAppearanceTrack(ConnectEntitiesCommand command, TimingInfo timing) {
        List<AnimationSegment> segments = List.of(AnimationSegment.opacity(timing.startTime(), timing.endTime(), resolveEasing(timing.easing())));
        return AnimationTrack.edgeTrack(command.id(), segments);
    }

    private Optional<AnimationTrack> arrowTrack(ConnectEntitiesParams params, TimingInfo timing) {
        double duration = timing.duration();
        if (duration <= 0) {
            return Optional.empty();
        }
        double arrowDelay = timing.startTime() + duration * DEFAULT_ARROW_DELAY_RATIO;
        double arrowDuration = duration * (1.0 - DEFAULT_ARROW_DELAY_RATIO);
        List<AnimationSegment> segments = List.of(AnimationSegment.opacity(arrowDelay, arrowDelay + arrowDuration, resolveEasing(timing.easing())));
        String edgeId = params.from() + "-" + params.to();
        return Optional.of(AnimationTrack.arrowTrack(edgeId, segments));
    }

    private List<AnimationTrack> createBehaviorTracks(AnimateBehaviorCommand command, TimingInfo timing, LayoutResult layoutResult) {
        AnimateBehaviorParams params = command.params();
        return switch (params.behavior()) {
            case "flow" -> createFlowTracks(params, command.id(), timing.startTime(), layoutResult.edges());
            case "orbit" -> createOrbitTracks(params, command.id(), timing.startTime(), layoutResult.nodes());
            default -> List.of();
        };
    }

    private List<AnimationTrack> createFlowTracks(AnimateBehaviorParams params, String commandId, double startTime, List<RoutedEdge> edges) {
        RoutedEdge edge = edges.stream()
            .filter(candidate -> candidate.from().equals(params.from()) && candidate.to().equals(params.to()))
            .findFirst()
            .orElse(null);
        if (edge == null || edge.startAnchor() == null || edge.endAnchor() == null) {
            return List.of();
        }
        List<com.dev.explainor.genesis.domain.Point> fullPath = new ArrayList<>();
        fullPath.add(edge.startAnchor());
        fullPath.addAll(edge.path());
        fullPath.add(edge.endAnchor());

        double pathLength = calculatePathLength(fullPath);
        double speed = params.speed() != null ? params.speed() : DEFAULT_FLOW_SPEED;
        double duration = params.duration() != null ? params.duration() : pathLength / speed;
        double endTime = startTime + duration;

        List<AnimationSegment> segments = new ArrayList<>();
        String easing = resolveBehaviorEasing(params.easing());
        segments.add(AnimationSegment.opacity(startTime, startTime + PARTICLE_FADE_DURATION, easing));

        double currentTime = startTime;

        for (int i = 0; i < fullPath.size() - 1; i++) {
            com.dev.explainor.genesis.domain.Point from = fullPath.get(i);
            com.dev.explainor.genesis.domain.Point to = fullPath.get(i + 1);
            double segmentLength = distance(from, to);
            double segmentDuration = duration == 0 ? 0 : (segmentLength / pathLength) * duration;
            segments.add(AnimationSegment.position(currentTime, currentTime + segmentDuration, easing, from.x(), from.y(), to.x(), to.y()));
            currentTime += segmentDuration;
        }
        segments.add(AnimationSegment.opacity(endTime - PARTICLE_FADE_DURATION, endTime, easing));
        String particleId = "particle-" + commandId;
        return List.of(AnimationTrack.particleTrack(particleId, segments));
    }

    private List<AnimationTrack> createOrbitTracks(AnimateBehaviorParams params, String commandId, double startTime, List<PositionedNode> nodes) {
        Map<String, PositionedNode> index = nodes.stream()
            .collect(Collectors.toMap(PositionedNode::id, Function.identity()));
        PositionedNode centerNode = index.get(params.from());
        if (centerNode == null) {
            return List.of();
        }
        double duration = params.duration() != null ? params.duration() : DEFAULT_ORBIT_DURATION;
        double endTime = startTime + duration;
        double centerX = centerNode.x();
        double centerY = centerNode.y();
        int steps = 60;
        List<AnimationSegment> segments = new ArrayList<>();
        String easing = resolveBehaviorEasing(params.easing());
        segments.add(AnimationSegment.opacity(startTime, startTime + PARTICLE_FADE_DURATION, easing));
        for (int i = 0; i < steps; i++) {
            double angle1 = (2 * Math.PI * i) / steps;
            double angle2 = (2 * Math.PI * (i + 1)) / steps;
            double x1 = centerX + DEFAULT_ORBIT_RADIUS * Math.cos(angle1);
            double y1 = centerY + DEFAULT_ORBIT_RADIUS * Math.sin(angle1);
            double x2 = centerX + DEFAULT_ORBIT_RADIUS * Math.cos(angle2);
            double y2 = centerY + DEFAULT_ORBIT_RADIUS * Math.sin(angle2);
            double segmentStart = startTime + (i * duration / steps);
            double segmentEnd = startTime + ((i + 1) * duration / steps);
            segments.add(AnimationSegment.position(segmentStart, segmentEnd, easing, x1, y1, x2, y2));
        }
        segments.add(AnimationSegment.opacity(endTime - PARTICLE_FADE_DURATION, endTime, easing));
        String particleId = "particle-" + commandId;
        return List.of(AnimationTrack.particleTrack(particleId, segments));
    }

    private AnimationTrack createFocusTrack(FocusOnCommand command, TimingInfo timing, List<PositionedNode> nodes) {
        FocusOnParams params = command.params();
        double duration = params.durationOrDefault(DEFAULT_FOCUS_DURATION);
        double targetScale = params.scale() != null ? params.scale() : DEFAULT_FOCUS_SCALE;
        double zoomLevel = params.zoomOrDefault(DEFAULT_FOCUS_ZOOM);
        double speed = params.speedOrDefault(DEFAULT_FOCUS_SPEED);
        double startTime = timing.startTime();
        double endTime = startTime + duration;
        PositionedNode focusNode = nodes.stream()
            .filter(node -> node.id().equals(params.target()))
            .findFirst()
            .orElse(null);
        List<AnimationSegment> segments = new ArrayList<>();
        if (focusNode != null) {
            segments.add(AnimationSegment.cameraPosition(startTime, endTime, resolveEasing(timing.easing()), focusNode.x(), focusNode.y(), focusNode.x(), focusNode.y()));
        }
        segments.add(AnimationSegment.scale(startTime, endTime, resolveEasing(timing.easing()), 1.0, targetScale));
        segments.add(AnimationSegment.zoom(startTime, endTime, resolveEasing(timing.easing()), 1.0, zoomLevel));
        segments.add(AnimationSegment.speed(startTime, endTime, resolveEasing(timing.easing()), 1.0, speed));
        return AnimationTrack.cameraTrack(segments);
    }

    private double calculatePathLength(List<com.dev.explainor.genesis.domain.Point> path) {
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += distance(path.get(i), path.get(i + 1));
        }
        return length;
    }

    private double distance(com.dev.explainor.genesis.domain.Point first, com.dev.explainor.genesis.domain.Point second) {
        return Math.hypot(second.x() - first.x(), second.y() - first.y());
    }

    private void addBreathingSegments(List<AnimationSegment> segments, double startTime) {
        double breathCycleDuration = 4.5;
        int breathCycles = 3;
        for (int i = 0; i < breathCycles; i++) {
            double cycleStart = startTime + i * breathCycleDuration;
            double cycleMid = cycleStart + breathCycleDuration / 2;
            double cycleEnd = cycleStart + breathCycleDuration;
            segments.add(AnimationSegment.scale(cycleStart, cycleMid, "easeInOutQuad", 1.0, 1.02));
            segments.add(AnimationSegment.scale(cycleMid, cycleEnd, "easeInOutQuad", 1.02, 1.0));
        }
    }

    private String resolveEasing(String easing) {
        return easing != null ? easing : "linear";
    }

    private String resolveBehaviorEasing(String easing) {
        return easing != null && !easing.isBlank() ? easing : "easeInOutQuint";
    }
}

