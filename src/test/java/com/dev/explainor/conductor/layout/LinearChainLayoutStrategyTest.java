package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LinearChainLayoutStrategyTest {

    private LinearChainLayoutStrategy strategy;
    private SceneState sceneState;

    @BeforeEach
    void setUp() {
        strategy = new LinearChainLayoutStrategy();
        Graph<String, DefaultEdge> emptyGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        sceneState = new SceneState(1280, 720, emptyGraph);
    }

    @Test
    void shouldPositionLinearChainHorizontally() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertEquals(3, positions.size());
        assertTrue(positions.get("node1").x() < positions.get("node2").x());
        assertTrue(positions.get("node2").x() < positions.get("node3").x());
        assertEquals(positions.get("node1").y(), positions.get("node2").y(), 0.1);
        assertEquals(positions.get("node2").y(), positions.get("node3").y(), 0.1);
    }

    @Test
    void shouldHandleGraphWithCycles() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");
        graph.addEdge("node3", "node1");

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertNotNull(positions);
        assertEquals(3, positions.size());
        assertTrue(positions.containsKey("node1"));
        assertTrue(positions.containsKey("node2"));
        assertTrue(positions.containsKey("node3"));
    }

    @Test
    void shouldHandleEmptyGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertNotNull(positions);
        assertTrue(positions.isEmpty());
    }

    @Test
    void shouldHandleSingleNode() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertEquals(1, positions.size());
        Point position = positions.get("node1");
        assertNotNull(position);
        assertTrue(position.x() >= 100);
        assertTrue(position.x() <= 1180);
        assertEquals(360, position.y(), 1.0);
    }

    @Test
    void shouldPositionAllNodesWithinCanvasBounds() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 5; i++) {
            graph.addVertex("node" + i);
            if (i > 0) {
                graph.addEdge("node" + (i - 1), "node" + i);
            }
        }

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertEquals(5, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= -200, "x=" + position.x());
            assertTrue(position.x() <= 1480, "x=" + position.x());
            assertTrue(position.y() >= -200, "y=" + position.y());
            assertTrue(position.y() <= 920, "y=" + position.y());
        }
    }

    @Test
    void shouldCenterVertically() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addEdge("node1", "node2");

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        double expectedY = 720 / 2.0;
        assertEquals(expectedY, positions.get("node1").y(), 1.0);
        assertEquals(expectedY, positions.get("node2").y(), 1.0);
    }

    @Test
    void shouldSpaceNodesEvenly() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        double spacing1 = positions.get("node2").x() - positions.get("node1").x();
        double spacing2 = positions.get("node3").x() - positions.get("node2").x();
        assertEquals(spacing1, spacing2, 1.0);
    }

    @Test
    void shouldReturnCorrectStrategyName() {
        assertEquals("Linear Chain", strategy.getName());
    }

    @Test
    void shouldHandleLongChain() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 10; i++) {
            graph.addVertex("node" + i);
            if (i > 0) {
                graph.addEdge("node" + (i - 1), "node" + i);
            }
        }

        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, stateWithGraph);

        assertEquals(10, positions.size());
        for (int i = 0; i < 9; i++) {
            assertTrue(positions.get("node" + i).x() < positions.get("node" + (i + 1)).x());
        }
    }
}

