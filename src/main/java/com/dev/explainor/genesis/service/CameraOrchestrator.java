package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.dto.AnimationSegment;
import com.dev.explainor.genesis.dto.AnimationTrack;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CameraOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(CameraOrchestrator.class);
    private static final Double DEFAULT_CAMERA_X = 640.0;
    private static final Double DEFAULT_CAMERA_Y = 360.0;
    private static final Double DEFAULT_ZOOM = 1.0;
    
    public AnimationTrack createFocusTrack(
            String targetId,
            Double startTime,
            Double duration,
            Double zoomLevel,
            List<PositionedNode> nodes) {
        
        PositionedNode targetNode = findNode(targetId, nodes);

        if (targetNode == null) {
            log.warn("Target node not found for focus: {}", targetId);
            return createDefaultCameraTrack(startTime, duration);
        }
        
        Double endTime = startTime + duration;
        List<AnimationSegment> segments = new ArrayList<>();
        
        segments.add(AnimationSegment.cameraPosition(
            startTime,
            endTime,
            "easeInOutQuad",
            DEFAULT_CAMERA_X,
            DEFAULT_CAMERA_Y,
            targetNode.x(),
            targetNode.y()
        ));
        
        segments.add(AnimationSegment.zoom(
            startTime,
            endTime,
            "easeInOutQuad",
            DEFAULT_ZOOM,
            zoomLevel
        ));
        
        log.info("Created focus track for '{}' at ({:.1f}, {:.1f}) with zoom {:.1f}",
            targetId, targetNode.x(), targetNode.y(), zoomLevel);
        
        return AnimationTrack.cameraTrack(segments);
    }
    
    private AnimationTrack createDefaultCameraTrack(Double startTime, Double duration) {
        return AnimationTrack.cameraTrack(List.of());
    }
    
    private PositionedNode findNode(String id, List<PositionedNode> nodes) {
        return nodes.stream()
            .filter(n -> n.id().equals(id))
            .findFirst()
            .orElse(null);
    }
}

