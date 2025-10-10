package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CameraOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(CameraOrchestrator.class);
    private static final double DEFAULT_CANVAS_WIDTH = 1280.0;
    private static final double DEFAULT_CANVAS_HEIGHT = 720.0;
    private static final double DEFAULT_ZOOM = 1.5;
    
    private final ViewportCalculator viewportCalculator;
    
    public CameraOrchestrator(ViewportCalculator viewportCalculator) {
        this.viewportCalculator = viewportCalculator;
    }
    
    public AnimationTrack createFocusTrack(
            String targetId,
            Double startTime,
            Double duration,
            Double zoomLevel,
            List<PositionedNode> nodes) {
        
        PositionedNode targetNode = findNode(targetId, nodes);
        if (targetNode == null) {
            log.warn("Target node not found for focus: {}", targetId);
            return AnimationTrack.cameraTrack(List.of()); // Return an empty camera track
        }
        
        double finalZoom = zoomLevel != null ? zoomLevel : DEFAULT_ZOOM;
        Viewport idealViewport = viewportCalculator.calculateFocus(targetNode);
        
        Viewport standardViewport = Viewport.standard(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
        
        double endTime = startTime + duration;
        List<AnimationSegment> segments = new ArrayList<>();
        
        segments.add(AnimationSegment.cameraPosition(
            startTime,
            endTime,
            "easeInOutQuad",
            standardViewport.centerX(),
            standardViewport.centerY(),
            idealViewport.centerX(),
            idealViewport.centerY()
        ));
        
        segments.add(AnimationSegment.zoom(
            startTime,
            endTime,
            "easeInOutQuad",
            standardViewport.zoom(),
            finalZoom
        ));
        
        log.info("Created focus track for '{}' at ({}, {}) with zoom {}",
            targetId, targetNode.x(), targetNode.y(), finalZoom);
        
        return AnimationTrack.cameraTrack(segments);
    }
    
    private PositionedNode findNode(String id, List<PositionedNode> nodes) {
        return nodes.stream()
            .filter(n -> n.id().equals(id))
            .findFirst()
            .orElse(null);
    }
}
