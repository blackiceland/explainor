package com.dev.explainor.genesis.layout;

import com.dev.explainor.genesis.config.LayoutProperties;
import com.dev.explainor.genesis.domain.Point;
import com.dev.explainor.genesis.layout.model.LayoutConstraints;
import com.dev.explainor.genesis.layout.model.LayoutEdge;
import com.dev.explainor.genesis.layout.model.PositionedNode;
import com.dev.explainor.genesis.layout.model.RoutedEdge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class OrthogonalPathFinder implements PathFinder {

    private final double gridStep;
    
    public OrthogonalPathFinder(LayoutProperties properties) {
        this.gridStep = properties.getGridStep();
    }

    @Override
    public List<RoutedEdge> routeEdges(List<LayoutEdge> edges, List<PositionedNode> nodes, LayoutConstraints constraints) {
        Objects.requireNonNull(edges);
        Objects.requireNonNull(nodes);
        List<RoutedEdge> result = new ArrayList<>();

        Set<GridPoint> blocked = buildBlockedCells(nodes);

        for (LayoutEdge edge : edges) {
            PositionedNode from = find(nodes, edge.from());
            PositionedNode to = find(nodes, edge.to());
            List<Point> path = route(from, to, blocked);
            result.add(new RoutedEdge(edge.id(), edge.from(), edge.to(), edge.label(), path));
        }
        return result;
    }

    private List<Point> route(PositionedNode from, PositionedNode to, Set<GridPoint> blocked) {
        GridPoint start = toGrid(from.x(), from.y());
        GridPoint goal = toGrid(to.x(), to.y());
        List<GridPoint> gridPath = bfs(start, goal, blocked);
        List<Point> points = new ArrayList<>();
        for (GridPoint gp : gridPath) {
            points.add(new Point(gp.x * gridStep, gp.y * gridStep));
        }
        return points;
    }

    private List<GridPoint> bfs(GridPoint start, GridPoint goal, Set<GridPoint> blocked) {
        Queue<GridPoint> queue = new ArrayDeque<>();
        Map<GridPoint, GridPoint> parent = new HashMap<>();
        Set<GridPoint> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        int minX = Math.min(start.x, goal.x) - 50;
        int maxX = Math.max(start.x, goal.x) + 50;
        int minY = Math.min(start.y, goal.y) - 50;
        int maxY = Math.max(start.y, goal.y) + 50;

        int iterations = 0;
        int maxIterations = 10000;

        while (!queue.isEmpty() && iterations++ < maxIterations) {
            GridPoint current = queue.remove();
            if (current.equals(goal)) break;
            for (GridPoint next : neighbors(current)) {
                if (next.x < minX || next.x > maxX || next.y < minY || next.y > maxY) continue;
                if (visited.contains(next)) continue;
                if (blocked.contains(next)) continue;
                visited.add(next);
                parent.put(next, current);
                queue.add(next);
            }
        }

        List<GridPoint> path = new ArrayList<>();
        GridPoint cur = goal;
        path.add(cur);
        while (!cur.equals(start) && parent.containsKey(cur)) {
            cur = parent.get(cur);
            path.add(0, cur);
        }
        if (!path.get(0).equals(start)) {
            path.clear();
            path.add(start);
            path.add(goal);
        }
        return simplify(path);
    }

    private List<GridPoint> neighbors(GridPoint p) {
        List<GridPoint> n = new ArrayList<>();
        n.add(new GridPoint(p.x + 1, p.y));
        n.add(new GridPoint(p.x - 1, p.y));
        n.add(new GridPoint(p.x, p.y + 1));
        n.add(new GridPoint(p.x, p.y - 1));
        return n;
    }

    private Set<GridPoint> buildBlockedCells(List<PositionedNode> nodes) {
        Set<GridPoint> blocked = new HashSet<>();
        for (PositionedNode n : nodes) {
            GridPoint gp = toGrid(n.x(), n.y());
            blocked.add(gp);
        }
        return blocked;
    }

    private PositionedNode find(List<PositionedNode> nodes, String id) {
        for (PositionedNode n : nodes) {
            if (n.id().equals(id)) return n;
        }
        return null;
    }

    private GridPoint toGrid(double x, double y) {
        long gx = Math.round(x / gridStep);
        long gy = Math.round(y / gridStep);
        return new GridPoint((int) gx, (int) gy);
    }

    private List<GridPoint> simplify(List<GridPoint> path) {
        if (path.size() <= 2) return path;
        List<GridPoint> out = new ArrayList<>();
        out.add(path.get(0));
        GridPoint prevDir = null;
        for (int i = 1; i < path.size(); i++) {
            GridPoint a = out.get(out.size() - 1);
            GridPoint b = path.get(i);
            GridPoint dir = new GridPoint(b.x - a.x, b.y - a.y);
            if (prevDir == null || !(dir.x == prevDir.x && dir.y == prevDir.y)) {
                out.add(b);
                prevDir = dir;
            } else {
                out.set(out.size() - 1, b);
            }
        }
        return out;
    }

}


