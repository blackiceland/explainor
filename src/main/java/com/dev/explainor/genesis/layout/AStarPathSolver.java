package com.dev.explainor.genesis.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarPathSolver {

    private final int explorationMargin;
    private final int maxIterations;

    public AStarPathSolver(int explorationMargin, int maxIterations) {
        this.explorationMargin = explorationMargin;
        this.maxIterations = maxIterations;
    }

    public List<GridPoint> findPath(GridPoint start, GridPoint goal, Set<GridPoint> blockedCells) {
        PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> Double.compare(a.fScore, b.fScore));
        Map<GridPoint, GridPoint> cameFrom = new HashMap<>();
        Map<GridPoint, Double> gScore = new HashMap<>();
        Set<GridPoint> closed = new HashSet<>();

        open.add(new Node(start, 0.0, heuristic(start, goal)));
        gScore.put(start, 0.0);

        int minX = Math.min(start.x(), goal.x()) - explorationMargin;
        int maxX = Math.max(start.x(), goal.x()) + explorationMargin;
        int minY = Math.min(start.y(), goal.y()) - explorationMargin;
        int maxY = Math.max(start.y(), goal.y()) + explorationMargin;

        int iterations = 0;

        while (!open.isEmpty() && iterations++ < maxIterations) {
            Node current = open.poll();
            if (current.point.equals(goal)) {
                return simplify(reconstructPath(cameFrom, current.point, start));
            }
            closed.add(current.point);

            for (GridPoint neighbor : neighbors(current.point)) {
                if (neighbor.x() < minX || neighbor.x() > maxX || neighbor.y() < minY || neighbor.y() > maxY) {
                    continue;
                }
                if (blockedCells.contains(neighbor) && !neighbor.equals(goal)) {
                    continue;
                }
                if (closed.contains(neighbor)) {
                    continue;
                }

                double tentativeScore = gScore.getOrDefault(current.point, Double.POSITIVE_INFINITY) + distance(current.point, neighbor);
                if (tentativeScore >= gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    continue;
                }

                cameFrom.put(neighbor, current.point);
                gScore.put(neighbor, tentativeScore);
                double fScore = tentativeScore + heuristic(neighbor, goal);
                open.add(new Node(neighbor, tentativeScore, fScore));
            }
        }

        List<GridPoint> fallback = new ArrayList<>();
        fallback.add(start);
        fallback.add(goal);
        return simplify(fallback);
    }

    private List<GridPoint> reconstructPath(Map<GridPoint, GridPoint> cameFrom, GridPoint current, GridPoint start) {
        List<GridPoint> path = new ArrayList<>();
        path.add(current);
        while (!current.equals(start) && cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        return path;
    }

    private double heuristic(GridPoint point, GridPoint goal) {
        return Math.abs(point.x() - goal.x()) + Math.abs(point.y() - goal.y());
    }

    private double distance(GridPoint first, GridPoint second) {
        return Math.hypot(second.x() - first.x(), second.y() - first.y());
    }

    private List<GridPoint> neighbors(GridPoint point) {
        List<GridPoint> neighbors = new ArrayList<>(4);
        neighbors.add(new GridPoint(point.x() + 1, point.y()));
        neighbors.add(new GridPoint(point.x() - 1, point.y()));
        neighbors.add(new GridPoint(point.x(), point.y() + 1));
        neighbors.add(new GridPoint(point.x(), point.y() - 1));
        return neighbors;
    }

    private List<GridPoint> simplify(List<GridPoint> path) {
        if (path.size() <= 2) {
            return path;
        }
        List<GridPoint> simplified = new ArrayList<>();
        simplified.add(path.get(0));
        GridPoint previousDirection = null;
        for (int i = 1; i < path.size(); i++) {
            GridPoint lastPoint = simplified.get(simplified.size() - 1);
            GridPoint currentPoint = path.get(i);
            GridPoint direction = new GridPoint(currentPoint.x() - lastPoint.x(), currentPoint.y() - lastPoint.y());
            if (previousDirection == null || directionNotEqual(previousDirection, direction)) {
                simplified.add(currentPoint);
                previousDirection = direction;
            } else {
                simplified.set(simplified.size() - 1, currentPoint);
            }
        }
        return simplified;
    }

    private boolean directionNotEqual(GridPoint previous, GridPoint current) {
        return previous.x() != current.x() || previous.y() != current.y();
    }

    private record Node(GridPoint point, double gScore, double fScore) {
    }
}
