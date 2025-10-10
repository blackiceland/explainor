package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.layout.model.BoundingBox;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.Viewport;
import org.springframework.stereotype.Component;

@Component
public class ViewportCalculator {
    
    private static final double DEFAULT_PADDING_FACTOR = 0.3;
    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 3.0;
    
    public Viewport calculateFocus(
            PositionedNode target,
            double canvasWidth,
            double canvasHeight) {
        return calculateFocus(target.boundingBox(), canvasWidth, canvasHeight);
    }
    
    public Viewport calculateFocus(
            BoundingBox target,
            double canvasWidth,
            double canvasHeight) {
        
        double paddingAmount = Math.max(target.width(), target.height()) * DEFAULT_PADDING_FACTOR;
        BoundingBox paddedBox = target.expand(paddingAmount);
        
        double requiredWidth = paddedBox.width();
        double requiredHeight = paddedBox.height();
        
        double zoomX = canvasWidth / requiredWidth;
        double zoomY = canvasHeight / requiredHeight;
        double zoom = Math.min(zoomX, zoomY);
        
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));
        
        return new Viewport(
            target.centerX(),
            target.centerY(),
            zoom
        );
    }
}

