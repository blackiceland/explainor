package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.SceneEntity;

import java.util.*;

@Deprecated(since = "Genesis", forRemoval = true)
public class PathFinder {

    private static final int MIN_GRID_CELL_SIZE = 10;
    private static final int MAX_GRID_CELL_SIZE = 40;
    private static final double ENTITY_PADDING = 20.0;

    public static List<Point> findOrthogonalPath(
            Point start, 
            Point end, 
            Collection<SceneEntity> obstacles,
            double canvasWidth,
            double canvasHeight) {

        if (obstacles.isEmpty() || !lineIntersectsAnyObstacle(start, end, obstacles)) {
            return List.of(start, end);
        }

        int gridCellSize = calculateOptimalCellSize(canvasWidth, canvasHeight);
        int gridWidth = (int) Math.ceil(canvasWidth / gridCellSize);
        int gridHeight = (int) Math.ceil(canvasHeight / gridCellSize);

        boolean[][] grid = createGrid(gridWidth, gridHeight, obstacles, gridCellSize);

        GridNode startNode = new GridNode(
                (int) (start.x() / gridCellSize),
                (int) (start.y() / gridCellSize)
        );
        GridNode endNode = new GridNode(
                (int) (end.x() / gridCellSize),
                (int) (end.y() / gridCellSize)
        );

        List<GridNode> path = aStarSearch(startNode, endNode, grid, gridWidth, gridHeight);

        if (path == null || path.isEmpty()) {
            return List.of(start, end);
        }

        return simplifyPath(convertToPoints(path, gridCellSize));
    }

    private static boolean lineIntersectsAnyObstacle(Point start, Point end, Collection<SceneEntity> obstacles) {
        for (SceneEntity obstacle : obstacles) {
            double obstacleMinX = obstacle.x() - ENTITY_PADDING;
            double obstacleMaxX = obstacle.x() + obstacle.width() + ENTITY_PADDING;
            double obstacleMinY = obstacle.y() - ENTITY_PADDING;
            double obstacleMaxY = obstacle.y() + obstacle.height() + ENTITY_PADDING;

            if (lineIntersectsRectangle(start.x(), start.y(), end.x(), end.y(),
                    obstacleMinX, obstacleMinY, obstacleMaxX, obstacleMaxY)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineIntersectsRectangle(double x1, double y1, double x2, double y2,
                                                   double rectMinX, double rectMinY, double rectMaxX, double rectMaxY) {
        if ((x1 < rectMinX && x2 < rectMinX) || (x1 > rectMaxX && x2 > rectMaxX) ||
            (y1 < rectMinY && y2 < rectMinY) || (y1 > rectMaxY && y2 > rectMaxY)) {
            return false;
        }

        if ((x1 >= rectMinX && x1 <= rectMaxX && y1 >= rectMinY && y1 <= rectMaxY) ||
            (x2 >= rectMinX && x2 <= rectMaxX && y2 >= rectMinY && y2 <= rectMaxY)) {
            return true;
        }

        double[] intersections = {
            lineIntersectsSegment(x1, y1, x2, y2, rectMinX, rectMinY, rectMaxX, rectMinY),
            lineIntersectsSegment(x1, y1, x2, y2, rectMaxX, rectMinY, rectMaxX, rectMaxY),
            lineIntersectsSegment(x1, y1, x2, y2, rectMaxX, rectMaxY, rectMinX, rectMaxY),
            lineIntersectsSegment(x1, y1, x2, y2, rectMinX, rectMaxY, rectMinX, rectMinY)
        };

        for (double t : intersections) {
            if (t >= 0 && t <= 1) {
                return true;
            }
        }
        return false;
    }

    private static double lineIntersectsSegment(double x1, double y1, double x2, double y2,
                                               double x3, double y3, double x4, double y4) {
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(denom) < 1e-10) {
            return -1;
        }
        double t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denom;
        return t;
    }

    private static int calculateOptimalCellSize(double canvasWidth, double canvasHeight) {
        double minDimension = Math.min(canvasWidth, canvasHeight);
        int cellSize = (int) (minDimension / 50);
        return Math.max(MIN_GRID_CELL_SIZE, Math.min(cellSize, MAX_GRID_CELL_SIZE));
    }

    private static boolean[][] createGrid(int width, int height, Collection<SceneEntity> obstacles, int gridCellSize) {
        boolean[][] grid = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = true;
            }
        }
        
        for (SceneEntity entity : obstacles) {
            int minX = (int) Math.max(0, (entity.x() - ENTITY_PADDING) / gridCellSize);
            int maxX = (int) Math.min(width - 1, (entity.x() + entity.width() + ENTITY_PADDING) / gridCellSize);
            int minY = (int) Math.max(0, (entity.y() - ENTITY_PADDING) / gridCellSize);
            int maxY = (int) Math.min(height - 1, (entity.y() + entity.height() + ENTITY_PADDING) / gridCellSize);

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    grid[y][x] = false;
                }
            }
        }

        return grid;
    }

    private static List<GridNode> aStarSearch(
            GridNode start,
            GridNode end,
            boolean[][] grid,
            int width,
            int height) {

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<GridNode> closedSet = new HashSet<>();
        Map<GridNode, GridNode> cameFrom = new HashMap<>();
        Map<GridNode, Double> gScore = new HashMap<>();

        gScore.put(start, 0.0);
        openSet.add(new AStarNode(start, 0.0, heuristic(start, end)));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            GridNode currentNode = current.node;

            if (currentNode.equals(end)) {
                return reconstructPath(cameFrom, currentNode);
            }

            closedSet.add(currentNode);

            for (GridNode neighbor : getNeighbors(currentNode, grid, width, height)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeG = gScore.get(currentNode) + 1.0;

                if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, currentNode);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic(neighbor, end);
                    openSet.add(new AStarNode(neighbor, tentativeG, f));
                }
            }
        }

        return null;
    }

    private static List<GridNode> getNeighbors(GridNode node, boolean[][] grid, int width, int height) {
        List<GridNode> neighbors = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            if (newX >= 0 && newX < width && newY >= 0 && newY < height && grid[newY][newX]) {
                neighbors.add(new GridNode(newX, newY));
            }
        }

        return neighbors;
    }

    private static double heuristic(GridNode a, GridNode b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static List<GridNode> reconstructPath(Map<GridNode, GridNode> cameFrom, GridNode current) {
        List<GridNode> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }

        return path;
    }

    private static List<Point> convertToPoints(List<GridNode> gridPath, int gridCellSize) {
        List<Point> points = new ArrayList<>();
        for (GridNode node : gridPath) {
            points.add(new Point(
                    node.x * gridCellSize + gridCellSize / 2.0,
                    node.y * gridCellSize + gridCellSize / 2.0
            ));
        }
        return points;
    }

    private static List<Point> simplifyPath(List<Point> path) {
        if (path.size() <= 2) {
            return path;
        }

        List<Point> simplified = new ArrayList<>();
        simplified.add(path.get(0));

        for (int i = 1; i < path.size() - 1; i++) {
            Point prev = path.get(i - 1);
            Point current = path.get(i);
            Point next = path.get(i + 1);

            if (!isCollinear(prev, current, next)) {
                simplified.add(current);
            }
        }

        simplified.add(path.get(path.size() - 1));
        return simplified;
    }

    private static boolean isCollinear(Point p1, Point p2, Point p3) {
        double dx1 = p2.x() - p1.x();
        double dy1 = p2.y() - p1.y();
        double dx2 = p3.x() - p2.x();
        double dy2 = p3.y() - p2.y();

        double crossProduct = dx1 * dy2 - dy1 * dx2;
        return Math.abs(crossProduct) < 1e-6;
    }

    private static class GridNode {
        final int x;
        final int y;

        GridNode(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GridNode)) return false;
            GridNode gridNode = (GridNode) o;
            return x == gridNode.x && y == gridNode.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class AStarNode {
        final GridNode node;
        final double g;
        final double f;

        AStarNode(GridNode node, double g, double f) {
            this.node = node;
            this.g = g;
            this.f = f;
        }
    }
}

