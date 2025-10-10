package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.AnimateBehaviorParams;
import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BehaviorFactoryTest {

    private BehaviorFactory behaviorFactory;

    @BeforeEach
    void setUp() {
        behaviorFactory = new BehaviorFactory();
    }

    @Test
    void testCreateFlowBehavior() {
        List<Point> path = List.of(
            new Point(100.0, 100.0),
            new Point(200.0, 100.0),
            new Point(200.0, 200.0)
        );
        
        RoutedEdge edge = new RoutedEdge("edge1", "nodeA", "nodeB", null, path);
        List<RoutedEdge> edges = List.of(edge);
        
        AnimateBehaviorParams params = AnimateBehaviorParams.flow("nodeA", "nodeB");
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(
            params,
            "flow1",
            0.0,
            List.of(),
            edges
        );
        
        assertEquals(1, tracks.size());
        AnimationTrack track = tracks.get(0);
        assertEquals("particle", track.type());
        assertEquals("particle-flow1", track.targetId());
        assertTrue(track.segments().size() > 0);
        
        List<AnimationSegment> positionSegments = track.segments().stream()
            .filter(s -> s.property().equals("position"))
            .toList();
        
        assertTrue(positionSegments.size() > 0);
    }

    @Test
    void testCreateOrbitBehavior() {
        PositionedNode centerNode = new PositionedNode("center", "Center", "⭕", 300.0, 300.0);
        List<PositionedNode> nodes = List.of(centerNode);
        
        AnimateBehaviorParams params = AnimateBehaviorParams.orbit("center", 3.0);
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(
            params,
            "orbit1",
            0.0,
            nodes,
            List.of()
        );
        
        assertEquals(1, tracks.size());
        AnimationTrack track = tracks.get(0);
        assertEquals("particle", track.type());
        assertEquals("particle-orbit1", track.targetId());
        
        List<AnimationSegment> positionSegments = track.segments().stream()
            .filter(s -> s.property().equals("position"))
            .toList();
        
        assertTrue(positionSegments.size() >= 60);
    }

    @Test
    void testFlowBehaviorWithMissingEdge() {
        AnimateBehaviorParams params = AnimateBehaviorParams.flow("nodeA", "nodeB");
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(
            params,
            "flow1",
            0.0,
            List.of(),
            List.of()
        );
        
        assertEquals(0, tracks.size());
    }

    @Test
    void testOrbitBehaviorWithMissingNode() {
        AnimateBehaviorParams params = AnimateBehaviorParams.orbit("center", 3.0);
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(
            params,
            "orbit1",
            0.0,
            List.of(),
            List.of()
        );
        
        assertEquals(0, tracks.size());
    }

    @Test
    void testFlowBehaviorWithCustomSpeed() {
        List<Point> path = List.of(
            new Point(0.0, 0.0),
            new Point(100.0, 0.0)
        );
        
        RoutedEdge edge = new RoutedEdge("edge1", "nodeA", "nodeB", null, path);
        
        AnimateBehaviorParams params = new AnimateBehaviorParams("flow", "nodeA", "nodeB", null, 100.0);
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(
            params,
            "flow1",
            5.0,
            List.of(),
            List.of(edge)
        );
        
        assertEquals(1, tracks.size());
        AnimationTrack track = tracks.get(0);
        
        double maxTime = track.segments().stream()
            .mapToDouble(AnimationSegment::endTime)
            .max()
            .orElse(0.0);
        
        assertTrue(maxTime > 5.0);
        assertTrue(maxTime < 7.0);
    }
}

