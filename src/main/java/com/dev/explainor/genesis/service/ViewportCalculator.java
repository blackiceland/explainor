package com.dev.explainor.genesis.service;

import com.dev.explainor.genesis.layout.model.BoundingBox;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.Viewport;
import org.springframework.stereotype.Component;

@Component
public class ViewportCalculator {
    
    public Viewport calculateFocus(PositionedNode target) {
        return calculateFocus(target.boundingBox());
    }
    
    public Viewport calculateFocus(BoundingBox target) {
        return new Viewport(
            target.centerX(),
            target.centerY(),
            1.0
        );
    }
}

