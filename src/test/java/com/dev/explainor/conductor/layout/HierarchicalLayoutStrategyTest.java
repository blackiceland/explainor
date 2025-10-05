package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HierarchicalLayoutStrategyTest {

    private HierarchicalLayoutStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HierarchicalLayoutStrategy();
    }

    @Test
    void shouldPositionSimpleTree() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addEdge("root", "child1");
        graph.addEdge("root", "child2");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(3, positions.size());
        assertTrue(positions.get("root").x() < positions.get("child1").x());
        assertTrue(positions.get("root").x() < positions.get("child2").x());
    }

    @Test
    void shouldPositionMultiLayerGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("layer0");
        graph.addVertex("layer1_a");
        graph.addVertex("layer1_b");
        graph.addVertex("layer2");
        graph.addEdge("layer0", "layer1_a");
        graph.addEdge("layer0", "layer1_b");
        graph.addEdge("layer1_a", "layer2");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(4, positions.size());
        assertTrue(positions.get("layer0").x() < positions.get("layer1_a").x());
        assertTrue(positions.get("layer1_a").x() < positions.get("layer2").x());
    }

    @Test
    void shouldHandleGraphWithNoRoots() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");
        graph.addEdge("node3", "node1");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertNotNull(positions);
        assertEquals(3, positions.size());
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
    void shouldAdaptLayerSpacingToCanvasWidth() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 6; i++) {
            graph.addVertex("layer" + i);
            if (i > 0) {
                graph.addEdge("layer" + (i - 1), "layer" + i);
            }
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(6, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= 0);
            assertTrue(position.x() <= 1280);
        }
    }

    @Test
    void shouldNotExceedCanvasBoundsWithManyLayers() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        for (int i = 0; i < 10; i++) {
            graph.addVertex("layer" + i);
            if (i > 0) {
                graph.addEdge("layer" + (i - 1), "layer" + i);
            }
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(10, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= 100);
            assertTrue(position.x() <= 1280);
        }
    }

    @Test
    void shouldCenterNodesVerticallyInEachLayer() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addVertex("child3");
        graph.addEdge("root", "child1");
        graph.addEdge("root", "child2");
        graph.addEdge("root", "child3");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        double avgY = (positions.get("child1").y() + 
                      positions.get("child2").y() + 
                      positions.get("child3").y()) / 3.0;
        assertEquals(360, avgY, 50);
    }

    @Test
    void shouldHandleMultipleRoots() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root1");
        graph.addVertex("root2");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addEdge("root1", "child1");
        graph.addEdge("root2", "child2");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(4, positions.size());
        assertEquals(positions.get("root1").x(), positions.get("root2").x(), 1.0);
    }

    @Test
    void shouldHandleDiamondPattern() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("top");
        graph.addVertex("middle1");
        graph.addVertex("middle2");
        graph.addVertex("bottom");
        graph.addEdge("top", "middle1");
        graph.addEdge("top", "middle2");
        graph.addEdge("middle1", "bottom");
        graph.addEdge("middle2", "bottom");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(4, positions.size());
        assertTrue(positions.get("top").x() < positions.get("middle1").x());
        assertTrue(positions.get("middle1").x() < positions.get("bottom").x());
    }

    @Test
    void shouldReturnCorrectStrategyName() {
        assertEquals("Hierarchical", strategy.getName());
    }

    @Test
    void shouldHandleWideTree() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        for (int i = 0; i < 8; i++) {
            String child = "child" + i;
            graph.addVertex(child);
            graph.addEdge("root", child);
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(9, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.y() >= -200);
            assertTrue(position.y() <= 920);
        }
    }
}

