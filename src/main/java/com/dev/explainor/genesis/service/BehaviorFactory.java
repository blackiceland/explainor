package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.AnimateBehaviorParams;
import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BehaviorFactory {
    
    private static final Logger log = LoggerFactory.getLogger(BehaviorFactory.class);
    private static final Double DEFAULT_FLOW_SPEED = 200.0;
    private static final Double DEFAULT_ORBIT_DURATION = 3.0;
    
    public List<AnimationTrack> createBehaviorTracks(
            AnimateBehaviorParams params,
            String commandId,
            Double startTime,
            List<PositionedNode> nodes,
            List<RoutedEdge> edges) {
        
        String behavior = params.behavior();
        
        return switch (behavior) {
            case "flow" -> createFlowBehavior(params, commandId, startTime, edges);
            case "orbit" -> createOrbitBehavior(params, commandId, startTime, nodes);
            default -> {
                log.warn("Unknown behavior: {}", behavior);
                yield List.of();
            }
        };
    }
    
    private List<AnimationTrack> createFlowBehavior(
            AnimateBehaviorParams params,
            String commandId,
            Double startTime,
            List<RoutedEdge> edges) {
        
        RoutedEdge edge = findEdge(params.from(), params.to(), edges);
        if (edge == null) {
            log.warn("Edge not found for flow: {} -> {}", params.from(), params.to());
            return List.of();
        }
        
        Point start = edge.effectiveStart();
        Point end = edge.effectiveEnd();
        
        List<Point> fullPath = new ArrayList<>();
        fullPath.add(start);
        fullPath.addAll(edge.path());
        fullPath.add(end);

        double pathLength = calculatePathLength(fullPath);
        double speed = params.speed() != null ? params.speed() : DEFAULT_FLOW_SPEED;
        double duration = pathLength / speed;
        double endTime = startTime + duration;
        
        List<AnimationSegment> segments = new ArrayList<>();
        
        segments.add(AnimationSegment.opacity(startTime, startTime + 0.2, "easeInOutQuint"));

        double currentTime = startTime;
        for (int i = 0; i < fullPath.size() - 1; i++) {
            Point from = fullPath.get(i);
            Point to = fullPath.get(i + 1);
            double segmentLength = distance(from, to);
            double segmentDuration = (segmentLength / pathLength) * duration;
            
            segments.add(AnimationSegment.position(
                currentTime,
                currentTime + segmentDuration,
                "easeInOutQuint",
                from.x(),
                from.y(),
                to.x(),
                to.y()
            ));
            
            currentTime += segmentDuration;
        }
        
        segments.add(AnimationSegment.opacity(endTime - 0.2, endTime, "easeInOutQuint"));
        
        String particleId = "particle-" + commandId;
        AnimationTrack track = AnimationTrack.particleTrack(particleId, segments);
        
        log.info("Created flow behavior: {} segments over {}s from ({},{}) to ({},{})",
            segments.size(), duration, start.x(), start.y(), end.x(), end.y());

        return List.of(track);
    }
    
    private List<AnimationTrack> createOrbitBehavior(
            AnimateBehaviorParams params,
            String commandId,
            double startTime,
            List<PositionedNode> nodes) {
        
        PositionedNode centerNode = findNode(params.from(), nodes);
        if (centerNode == null) {
            log.warn("Center node not found for orbit: {}", params.from());
            return List.of();
        }

        double duration = params.duration() != null ? params.duration() : DEFAULT_ORBIT_DURATION;
        double endTime = startTime + duration;
        double centerX = centerNode.x();
        double centerY = centerNode.y();
        double radius = 80.0;
        
        int steps = 60;
        List<AnimationSegment> segments = new ArrayList<>();
        
        segments.add(AnimationSegment.opacity(startTime, startTime + 0.2, "easeInOutQuint"));
        
        for (int i = 0; i < steps; i++) {
            double angle1 = (2 * Math.PI * i) / steps;
            double angle2 = (2 * Math.PI * (i + 1)) / steps;

            double x1 = centerX + radius * Math.cos(angle1);
            double y1 = centerY + radius * Math.sin(angle1);
            double x2 = centerX + radius * Math.cos(angle2);
            double y2 = centerY + radius * Math.sin(angle2);

            double segmentStart = startTime + (i * duration / steps);
            double segmentEnd = startTime + ((i + 1) * duration / steps);

            segments.add(AnimationSegment.position(
                segmentStart,
                segmentEnd,
                "linear",
                x1,
                y1,
                x2,
                y2
            ));
        }
        
        segments.add(AnimationSegment.opacity(endTime - 0.2, endTime, "easeInOutQuint"));
        
        String particleId = "particle-" + commandId;
        AnimationTrack track = AnimationTrack.particleTrack(particleId, segments);
        
        log.info("Created orbit behavior: {} segments over {}s", segments.size(), duration);
        return List.of(track);
    }
    
    private RoutedEdge findEdge(String from, String to, List<RoutedEdge> edges) {
        return edges.stream()
            .filter(e -> e.from().equals(from) && e.to().equals(to))
            .findFirst()
            .orElse(null);
    }
    
    private PositionedNode findNode(String id, List<PositionedNode> nodes) {
        return nodes.stream()
            .filter(n -> n.id().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    private Double calculatePathLength(List<Point> path) {
        double length = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += distance(path.get(i), path.get(i + 1));
        }
        return length;
    }
    
    private double distance(Point p1, Point p2) {
        double dx = p2.x() - p1.x();
        double dy = p2.y() - p1.y();

        return Math.sqrt(dx * dx + dy * dy);
    }
}

