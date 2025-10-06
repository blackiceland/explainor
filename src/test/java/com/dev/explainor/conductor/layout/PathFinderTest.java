package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.SceneEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Legacy conductor code - replaced by Genesis layout engine")
class PathFinderTest {

    private static final double CANVAS_WIDTH = 1280.0;
    private static final double CANVAS_HEIGHT = 720.0;

    @Test
    void shouldFindDirectPathWithNoObstacles() {
        Point start = new Point(100, 100);
        Point end = new Point(500, 100);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(start.x(), path.get(0).x(), 20);
        assertEquals(start.y(), path.get(0).y(), 20);
        assertEquals(end.x(), path.get(path.size() - 1).x(), 20);
        assertEquals(end.y(), path.get(path.size() - 1).y(), 20);
    }

    @Test
    void shouldFindPathAroundSingleObstacle() {
        Point start = new Point(100, 360);
        Point end = new Point(500, 360);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("obstacle", 250, 300, 100, 120)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(path.size() >= 2);
    }

    @Test
    void shouldFindPathThroughNarrowCorridor() {
        Point start = new Point(100, 360);
        Point end = new Point(700, 360);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("top", 300, 100, 200, 150),
            new SceneEntity("bottom", 300, 470, 200, 150)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertTrue(path.size() >= 2);
    }

    @Test
    void shouldSimplifyCollinearPoints() {
        Point start = new Point(100, 100);
        Point end = new Point(100, 500);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertTrue(path.size() <= 3);
    }

    @Test
    void shouldHandleStartEqualsEnd() {
        Point start = new Point(300, 300);
        Point end = new Point(300, 300);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertTrue(path.size() >= 1);
        assertEquals(start.x(), path.get(0).x(), 20);
        assertEquals(start.y(), path.get(0).y(), 20);
    }

    @Test
    void shouldReturnFallbackPathWhenNoPathExists() {
        Point start = new Point(100, 360);
        Point end = new Point(700, 360);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("wall", 300, 0, 100, 720)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertEquals(2, path.size());
        assertEquals(start, path.get(0));
        assertEquals(end, path.get(1));
    }

    @Test
    void shouldCalculateOptimalCellSizeForStandardCanvas() {
        Point start = new Point(100, 100);
        Point end = new Point(200, 200);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, 1280, 720);

        assertNotNull(path);
    }

    @Test
    void shouldCalculateOptimalCellSizeFor4KCanvas() {
        Point start = new Point(100, 100);
        Point end = new Point(200, 200);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, 3840, 2160);

        assertNotNull(path);
    }

    @Test
    void shouldCalculateOptimalCellSizeForMobileCanvas() {
        Point start = new Point(100, 100);
        Point end = new Point(200, 200);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, 640, 360);

        assertNotNull(path);
    }

    @Test
    void shouldHandleMultipleObstacles() {
        Point start = new Point(100, 360);
        Point end = new Point(900, 360);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("obs1", 200, 300, 80, 120),
            new SceneEntity("obs2", 400, 300, 80, 120),
            new SceneEntity("obs3", 600, 300, 80, 120)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertTrue(path.size() >= 2);
    }

    @Test
    void shouldRespectEntityPadding() {
        Point start = new Point(100, 360);
        Point end = new Point(500, 360);
        SceneEntity obstacle = new SceneEntity("obstacle", 250, 300, 100, 120);
        List<SceneEntity> obstacles = List.of(obstacle);

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        for (Point point : path) {
            double distToObstacle = Math.min(
                Math.abs(point.x() - obstacle.x()),
                Math.abs(point.x() - (obstacle.x() + obstacle.width()))
            );
            if (point.y() >= obstacle.y() && point.y() <= obstacle.y() + obstacle.height()) {
                assertTrue(distToObstacle > 5);
            }
        }
    }

    @Test
    void shouldFindPathInComplexMaze() {
        Point start = new Point(50, 50);
        Point end = new Point(1200, 650);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("wall1", 200, 0, 50, 300),
            new SceneEntity("wall2", 200, 400, 50, 320),
            new SceneEntity("wall3", 400, 100, 50, 300),
            new SceneEntity("wall4", 400, 500, 50, 220),
            new SceneEntity("wall5", 600, 0, 50, 400),
            new SceneEntity("wall6", 600, 500, 50, 220)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertTrue(path.size() >= 2);
    }

    @Test
    void shouldProduceOrthogonalPath() {
        Point start = new Point(100, 100);
        Point end = new Point(500, 500);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            boolean isHorizontal = Math.abs(p1.y() - p2.y()) < 1e-6;
            boolean isVertical = Math.abs(p1.x() - p2.x()) < 1e-6;
            assertTrue(isHorizontal || isVertical);
        }
    }

    @Test
    void shouldHandleEmptyObstaclesList() {
        Point start = new Point(200, 200);
        Point end = new Point(800, 500);
        List<SceneEntity> obstacles = new ArrayList<>();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    void shouldHandleObstacleAtStartPosition() {
        Point start = new Point(300, 300);
        Point end = new Point(700, 300);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("start_obstacle", 280, 280, 40, 40)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
    }

    @Test
    void shouldHandleObstacleAtEndPosition() {
        Point start = new Point(300, 300);
        Point end = new Point(700, 300);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("end_obstacle", 680, 280, 40, 40)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
    }

    @Test
    void shouldFindShortestPathAmongMultipleOptions() {
        Point start = new Point(100, 360);
        Point end = new Point(900, 360);
        List<SceneEntity> obstacles = List.of(
            new SceneEntity("middle", 450, 300, 100, 120)
        );

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        assertTrue(path.size() <= 10);
    }

    @Test
    void shouldHandleVeryLargeCanvas() {
        Point start = new Point(100, 100);
        Point end = new Point(5000, 3000);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, 5120, 3200);

        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    void shouldHandleVerySmallCanvas() {
        Point start = new Point(50, 50);
        Point end = new Point(250, 150);
        List<SceneEntity> obstacles = List.of();

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, 320, 180);

        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    void shouldNotPassThroughObstacles() {
        Point start = new Point(100, 360);
        Point end = new Point(700, 360);
        SceneEntity obstacle = new SceneEntity("block", 350, 310, 100, 100);
        List<SceneEntity> obstacles = List.of(obstacle);

        List<Point> path = PathFinder.findOrthogonalPath(start, end, obstacles, CANVAS_WIDTH, CANVAS_HEIGHT);

        assertNotNull(path);
        for (Point point : path) {
            boolean isInside = point.x() >= obstacle.x() - 10 &&
                             point.x() <= obstacle.x() + obstacle.width() + 10 &&
                             point.y() >= obstacle.y() - 10 &&
                             point.y() <= obstacle.y() + obstacle.height() + 10;
            if (isInside) {
                double distToEdge = Math.min(
                    Math.min(Math.abs(point.x() - obstacle.x()), 
                            Math.abs(point.x() - (obstacle.x() + obstacle.width()))),
                    Math.min(Math.abs(point.y() - obstacle.y()), 
                            Math.abs(point.y() - (obstacle.y() + obstacle.height())))
                );
                assertTrue(distToEdge > 5);
            }
        }
    }
}

