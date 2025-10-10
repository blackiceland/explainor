/*
package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.domain.AnimateBehaviorParams;
import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BehaviorFactoryTest {

    private final BehaviorFactory behaviorFactory = new BehaviorFactory();

    @Test
    void shouldCreateFlowBehaviorWithCorrectSegments() {
        AnimateBehaviorParams params = AnimateBehaviorParams.flow("A", "B");
        RoutedEdge edge = new RoutedEdge("edge1", "A", "B", null, null, List.of(new Point(100, 100), new Point(200, 100)), new Point(50, 100), new Point(250, 100), 200.0);
        
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(), List.of(edge));

        assertEquals(1, tracks.size());
        AnimationTrack track = tracks.get(0);
        assertEquals("particle", track.type());

        List<AnimationSegment> segments = track.segments();
        assertEquals(4, segments.size()); // Opacity in, 2x Position, Opacity out
        
        AnimationSegment pos1 = segments.get(1);
        assertEquals("position", pos1.property());
        assertEquals(new AnimationSegment.PositionValue(50, 100), pos1.fromValue());
        assertEquals(new AnimationSegment.PositionValue(100, 100), pos1.toValue());

        AnimationSegment pos2 = segments.get(2);
        assertEquals("position", pos2.property());
        assertEquals(new AnimationSegment.PositionValue(200, 100), pos2.fromValue());
        assertEquals(new AnimationSegment.PositionValue(250, 100), pos2.toValue());
    }

    @Test
    void shouldReturnEmptyListForMissingEdgeInFlow() {
        AnimateBehaviorParams params = AnimateBehaviorParams.flow("A", "C");
        RoutedEdge edge = new RoutedEdge("edge1", "A", "B", null, null, List.of(new Point(100, 100)), new Point(50, 100), new Point(250, 100), 200.0);

        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(), List.of(edge));

        assertTrue(tracks.isEmpty());
    }

    @Test
    void shouldCreateOrbitBehavior() {
        AnimateBehaviorParams params = new AnimateBehaviorParams("orbit", "center", null, 3.0, null);
        PositionedNode centerNode = new PositionedNode("center", "Center", "icon", 500, 500, 100, 100);

        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(centerNode), List.of());

        assertEquals(1, tracks.size());
        AnimationTrack track = tracks.get(0);
        assertTrue(track.segments().size() > 60); // Opacity + 60 position segments + Opacity
    }

    @Test
    void shouldReturnEmptyListForMissingNodeInOrbit() {
        AnimateBehaviorParams params = new AnimateBehaviorParams("orbit", "nonexistent", null, 3.0, null);
        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(), List.of());
        assertTrue(tracks.isEmpty());
    }

    @Test
    void shouldCalculateCorrectDurationBasedOnSpeed() {
        AnimateBehaviorParams params = new AnimateBehaviorParams("flow", "A", "B", null, 100.0);
        RoutedEdge edge = new RoutedEdge("edge1", "A", "B", null, null, List.of(new Point(100, 100)), new Point(50, 100), new Point(150, 100), 100.0);

        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(), List.of(edge));

        AnimationTrack track = tracks.get(0);
        double maxTime = track.segments().stream().mapToDouble(AnimationSegment::endTime).max().orElse(0);
        
        // Path length is 100 (50 to 100, 100 to 150), speed is 100.0, so duration should be 1.0s
        assertEquals(1.0, maxTime, 0.01);
    }
    
    @Test
    void shouldUseExplicitDurationWhenProvided() {
        AnimateBehaviorParams params = new AnimateBehaviorParams("flow", "A", "B", 5.0, 100.0); // Duration should override speed
        RoutedEdge edge = new RoutedEdge("edge1", "A", "B", null, null, List.of(), new Point(0, 0), new Point(100, 0), 100.0);

        List<AnimationTrack> tracks = behaviorFactory.createBehaviorTracks(params, "cmd1", 0.0, List.of(), List.of(edge));
        
        AnimationTrack track = tracks.get(0);
        double maxTime = track.segments().stream().mapToDouble(AnimationSegment::endTime).max().orElse(0);
        
        assertEquals(5.0, maxTime, 0.01);
    }
}
*/

