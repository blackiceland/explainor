package com.dev.explainor.conductor.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrowRoutingHelperTest {

    private static final double ENTITY_WIDTH = 200.0;
    private static final double ENTITY_HEIGHT = 120.0;

    @Test
    void shouldCalculateEdgePointToTheRight() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(800, 300);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertEquals(500 + ENTITY_WIDTH / 2, edgePoint.x(), 1.0);
        assertEquals(300, edgePoint.y(), 1.0);
    }

    @Test
    void shouldCalculateEdgePointToTheLeft() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(200, 300);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertEquals(500 - ENTITY_WIDTH / 2, edgePoint.x(), 1.0);
        assertEquals(300, edgePoint.y(), 1.0);
    }

    @Test
    void shouldCalculateEdgePointToTheTop() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(500, 100);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertEquals(500, edgePoint.x(), 1.0);
        assertEquals(300 - ENTITY_HEIGHT / 2, edgePoint.y(), 1.0);
    }

    @Test
    void shouldCalculateEdgePointToTheBottom() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(500, 500);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertEquals(500, edgePoint.x(), 1.0);
        assertEquals(300 + ENTITY_HEIGHT / 2, edgePoint.y(), 1.0);
    }

    @Test
    void shouldCalculateEdgePointDiagonallyTopRight() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(700, 150);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        double distFromCenter = Math.sqrt(
            Math.pow(edgePoint.x() - entityCenter.x(), 2) +
            Math.pow(edgePoint.y() - entityCenter.y(), 2)
        );
        assertTrue(distFromCenter <= Math.sqrt(Math.pow(ENTITY_WIDTH/2, 2) + Math.pow(ENTITY_HEIGHT/2, 2)) + 1);
    }

    @Test
    void shouldCalculateEdgePointDiagonallyBottomLeft() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(300, 450);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertTrue(edgePoint.x() <= entityCenter.x());
        assertTrue(edgePoint.x() >= entityCenter.x() - ENTITY_WIDTH / 2);
        assertTrue(edgePoint.y() >= entityCenter.y());
        assertTrue(edgePoint.y() <= entityCenter.y() + ENTITY_HEIGHT / 2);
    }

    @Test
    void shouldHandleSamePoint() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(500, 300);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertNotNull(edgePoint);
    }

    @Test
    void shouldCalculateEdgePointOnBoundary() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(700, 300);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        double distanceFromCenter = Math.sqrt(
            Math.pow(edgePoint.x() - entityCenter.x(), 2) +
            Math.pow(edgePoint.y() - entityCenter.y(), 2)
        );

        assertTrue(distanceFromCenter <= ENTITY_WIDTH / 2 + 1);
    }

    @Test
    void shouldHandleVeryCloseTargets() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(501, 301);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertNotNull(edgePoint);
        assertTrue(Math.abs(edgePoint.x() - entityCenter.x()) <= ENTITY_WIDTH / 2 + 1);
        assertTrue(Math.abs(edgePoint.y() - entityCenter.y()) <= ENTITY_HEIGHT / 2 + 1);
    }

    @Test
    void shouldHandleVeryDistantTargets() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(5000, 3000);

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, ENTITY_WIDTH, ENTITY_HEIGHT
        );

        assertNotNull(edgePoint);
        assertTrue(Math.abs(edgePoint.x() - entityCenter.x()) <= ENTITY_WIDTH / 2 + 1);
        assertTrue(Math.abs(edgePoint.y() - entityCenter.y()) <= ENTITY_HEIGHT / 2 + 1);
    }

    @Test
    void shouldHandleDifferentEntitySizes() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(800, 300);
        double customWidth = 300.0;
        double customHeight = 150.0;

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, customWidth, customHeight
        );

        assertEquals(500 + customWidth / 2, edgePoint.x(), 1.0);
        assertEquals(300, edgePoint.y(), 1.0);
    }

    @Test
    void shouldHandleSmallEntitySizes() {
        Point entityCenter = new Point(500, 300);
        Point targetCenter = new Point(800, 300);
        double smallWidth = 50.0;
        double smallHeight = 30.0;

        Point edgePoint = ArrowRoutingHelper.calculateEdgePoint(
            entityCenter, targetCenter, smallWidth, smallHeight
        );

        assertEquals(500 + smallWidth / 2, edgePoint.x(), 1.0);
        assertEquals(300, edgePoint.y(), 1.0);
    }
}

