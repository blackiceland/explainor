/*
package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.Viewport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CameraOrchestratorTest {

    private CameraOrchestrator cameraOrchestrator;
    private ViewportCalculator viewportCalculator;

    @BeforeEach
    void setUp() {
        viewportCalculator = Mockito.mock(ViewportCalculator.class);
        cameraOrchestrator = new CameraOrchestrator(viewportCalculator);
    }

    @Test
    void shouldCreateFocusTrackWithCorrectSegments() {
        PositionedNode target = new PositionedNode("target1", "Target", "icon", 800, 400, 120, 80);
        Viewport idealViewport = new Viewport(740, 360, 1.2);
        
        when(viewportCalculator.calculateFocus(any(PositionedNode.class))).thenReturn(idealViewport);

        AnimationTrack track = cameraOrchestrator.createFocusTrack("target1", 1.0, 1.5, null, List.of(target));
        
        assertNotNull(track);
        assertEquals("camera", track.type());
        
        List<AnimationSegment> segments = track.segments();
        assertEquals(2, segments.size());
        
        AnimationSegment posSegment = segments.stream().filter(s -> s.property().equals("cameraPosition")).findFirst().orElse(null);
        assertNotNull(posSegment);
        assertEquals(1.0, posSegment.startTime());
        assertEquals(2.5, posSegment.endTime());
        assertEquals(new AnimationSegment.PositionValue(640, 360), posSegment.fromValue());
        assertEquals(new AnimationSegment.PositionValue(800, 400), posSegment.toValue());

        AnimationSegment zoomSegment = segments.stream().filter(s -> s.property().equals("zoom")).findFirst().orElse(null);
        assertNotNull(zoomSegment);
        assertEquals(1.0, zoomSegment.fromValue());
        assertEquals(1.5, zoomSegment.toValue()); // default zoom
    }

    @Test
    void shouldAcceptCustomZoomLevel() {
        PositionedNode target = new PositionedNode("target1", "Target", "icon", 800, 400, 120, 80);
        when(viewportCalculator.calculateFocus(any(PositionedNode.class))).thenReturn(new Viewport(740, 360, 2.5));

        AnimationTrack track = cameraOrchestrator.createFocusTrack("target1", 1.0, 1.5, 3.0, List.of(target));

        AnimationSegment zoomSegment = track.segments().stream().filter(s -> s.property().equals("zoom")).findFirst().orElse(null);
        assertNotNull(zoomSegment);
        assertEquals(3.0, zoomSegment.toValue());
    }
    
    @Test
    void shouldReturnEmptyTrackIfTargetNodeNotFound() {
        when(viewportCalculator.calculateFocus(any(PositionedNode.class))).thenReturn(new Viewport(0, 0, 1));
        
        AnimationTrack track = cameraOrchestrator.createFocusTrack("nonexistent", 1.0, 1.5, null, List.of());
        
        assertNotNull(track);
        assertTrue(track.segments().isEmpty());
    }
}
*/

