package com.dev.explainor.helios.layout;

public class ArrowRoutingHelper {

    public static Point calculateEdgePoint(
            Point entityCenter, 
            Point targetCenter, 
            double entityWidth, 
            double entityHeight) {
        double dx = targetCenter.x() - entityCenter.x();
        double dy = targetCenter.y() - entityCenter.y();

        double angle = Math.atan2(dy, dx);

        double halfWidth = entityWidth / 2.0;
        double halfHeight = entityHeight / 2.0;

        double absAngle = Math.abs(angle);
        double cornerAngle = Math.atan2(halfHeight, halfWidth);

        double edgeX;
        double edgeY;

        if (absAngle < cornerAngle) {
            edgeX = entityCenter.x() + halfWidth;
            edgeY = entityCenter.y() + halfWidth * Math.tan(angle);
        } else if (absAngle < Math.PI - cornerAngle) {
            double sign = angle > 0 ? 1 : -1;
            edgeX = entityCenter.x() + halfHeight / Math.tan(angle);
            edgeY = entityCenter.y() + sign * halfHeight;
        } else {
            edgeX = entityCenter.x() - halfWidth;
            edgeY = entityCenter.y() - halfWidth * Math.tan(angle);
        }

        return new Point(edgeX, edgeY);
    }
}

