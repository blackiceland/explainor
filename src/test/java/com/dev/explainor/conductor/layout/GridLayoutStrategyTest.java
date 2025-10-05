package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GridLayoutStrategyTest {

    private GridLayoutStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new GridLayoutStrategy();
    }

    @Test
    void shouldPositionNodesInGrid() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addVertex("node4");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(4, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= 0);
            assertTrue(position.x() <= 1280);
            assertTrue(position.y() >= 0);
            assertTrue(position.y() <= 720);
        }
    }

    @Test
    void shouldHandleEmptyGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertNotNull(positions);
        assertTrue(positions.isEmpty());
    }

    @Test
    void shouldHandleSingleNode() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(1, positions.size());
        assertNotNull(positions.get("node1"));
    }

    @Test
    void shouldHandleManyNodes() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 20; i++) {
            graph.addVertex("node" + i);
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(20, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= -500, "x=" + position.x());
            assertTrue(position.x() <= 1780, "x=" + position.x());
            assertTrue(position.y() >= -500, "y=" + position.y());
            assertTrue(position.y() <= 1220, "y=" + position.y());
        }
    }

    @Test
    void shouldNotOverlapNodes() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 9; i++) {
            graph.addVertex("node" + i);
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(9, positions.size());
        for (Map.Entry<String, Point> entry1 : positions.entrySet()) {
            for (Map.Entry<String, Point> entry2 : positions.entrySet()) {
                if (!entry1.getKey().equals(entry2.getKey())) {
                    double distance = Math.sqrt(
                        Math.pow(entry1.getValue().x() - entry2.getValue().x(), 2) +
                        Math.pow(entry1.getValue().y() - entry2.getValue().y(), 2)
                    );
                    assertTrue(distance > 50);
                }
            }
        }
    }

    @Test
    void shouldReturnCorrectStrategyName() {
        assertEquals("Grid", strategy.getName());
    }
}

