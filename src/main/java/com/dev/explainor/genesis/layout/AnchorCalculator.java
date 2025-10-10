package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.layout.model.BoundingBox;
import com.dev.explainor.genesis.layout.model.PositionedNode;

public class AnchorCalculator {
    
    public static Point calculateEntryPoint(PositionedNode node, Point fromPoint) {
        BoundingBox box = node.boundingBox();
        double centerX = node.x();
        double centerY = node.y();
        
        double dx = fromPoint.x() - centerX;
        double dy = fromPoint.y() - centerY;
        
        if (Math.abs(dx) < 0.001 && Math.abs(dy) < 0.001) {
            return new Point(box.x() + box.width(), centerY);
        }
        
        double angle = Math.atan2(dy, dx);
        
        double halfWidth = node.width() / 2;
        double halfHeight = node.height() / 2;
        
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        
        double t;
        if (Math.abs(cos) > Math.abs(sin)) {
            t = halfWidth / Math.abs(cos);
        } else {
            t = halfHeight / Math.abs(sin);
        }
        
        double x = centerX + t * cos;
        double y = centerY + t * sin;
        
        return new Point(x, y);
    }
    
    public static Point calculateExitPoint(PositionedNode node, Point toPoint) {
        return calculateEntryPoint(node, toPoint);
    }
}

