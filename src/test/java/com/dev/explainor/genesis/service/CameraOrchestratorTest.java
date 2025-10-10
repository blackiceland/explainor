package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CameraOrchestratorTest {

    private CameraOrchestrator cameraOrchestrator;
    private ViewportCalculator viewportCalculator;

    @BeforeEach
    void setUp() {
        viewportCalculator = new ViewportCalculator();
        cameraOrchestrator = new CameraOrchestrator(viewportCalculator);
    }

    @Test
    void testCreateFocusTrack() {
        PositionedNode targetNode = new PositionedNode("server", "Server", "🖥️", 400.0, 300.0, 120.0, 80.0);
        List<PositionedNode> nodes = List.of(targetNode);
        
        AnimationTrack track = cameraOrchestrator.createFocusTrack(
            "server",
            2.0,
            1.5,
            nodes
        );
        
        assertNotNull(track);
        assertEquals("camera", track.type());
        assertEquals("main-camera", track.targetId());
        
        List<AnimationSegment> cameraPositionSegments = track.segments().stream()
            .filter(s -> s.property().equals("cameraPosition"))
            .toList();
        
        assertEquals(1, cameraPositionSegments.size());
        
        AnimationSegment posSegment = cameraPositionSegments.get(0);
        assertEquals(2.0, posSegment.startTime());
        assertEquals(3.5, posSegment.endTime());
        
        List<AnimationSegment> zoomSegments = track.segments().stream()
            .filter(s -> s.property().equals("zoom"))
            .toList();
        
        assertEquals(1, zoomSegments.size());
        
        AnimationSegment zoomSegment = zoomSegments.get(0);
        assertEquals(1.0, zoomSegment.fromValue());
        assertEquals(1.0, zoomSegment.toValue());
    }

    @Test
    void testCreateFocusTrackWithMissingNode() {
        AnimationTrack track = cameraOrchestrator.createFocusTrack(
            "nonexistent",
            0.0,
            1.5,
            List.of()
        );
        
        assertNotNull(track);
        assertEquals("camera", track.type());
        assertEquals(0, track.segments().size());
    }

    @Test
    void testFocusTrackTimings() {
        PositionedNode node = new PositionedNode("node1", "Node 1", "📦", 200.0, 150.0, 120.0, 80.0);
        
        AnimationTrack track = cameraOrchestrator.createFocusTrack(
            "node1",
            5.0,
            2.0,
            List.of(node)
        );
        
        track.segments().forEach(segment -> {
            assertEquals(5.0, segment.startTime());
            assertEquals(7.0, segment.endTime());
        });
    }
}

