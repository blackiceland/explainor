package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HubAndSpokeLayoutStrategyTest {

    private HubAndSpokeLayoutStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HubAndSpokeLayoutStrategy();
    }

    @Test
    void shouldPositionHubInCenter() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addEdge("hub", "spoke1");
        graph.addEdge("hub", "spoke2");
        graph.addEdge("hub", "spoke3");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        Point hubPosition = positions.get("hub");
        assertNotNull(hubPosition);
        assertEquals(640, hubPosition.x(), 50);
        assertEquals(360, hubPosition.y(), 50);
    }

    @Test
    void shouldPositionSpokesRadially() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addVertex("spoke4");
        graph.addEdge("hub", "spoke1");
        graph.addEdge("hub", "spoke2");
        graph.addEdge("hub", "spoke3");
        graph.addEdge("hub", "spoke4");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(5, positions.size());
        Point hubPosition = positions.get("hub");
        
        for (int i = 1; i <= 4; i++) {
            Point spokePosition = positions.get("spoke" + i);
            assertNotNull(spokePosition);
            double distance = Math.sqrt(
                Math.pow(spokePosition.x() - hubPosition.x(), 2) +
                Math.pow(spokePosition.y() - hubPosition.y(), 2)
            );
            assertTrue(distance > 100);
        }
    }

    @Test
    void shouldHandleIncomingEdgesToHub() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addEdge("spoke1", "hub");
        graph.addEdge("spoke2", "hub");
        graph.addEdge("spoke3", "hub");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(4, positions.size());
        assertNotNull(positions.get("hub"));
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
    void shouldHandleGraphWithNoHub() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addEdge("node1", "node2");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertNotNull(positions);
        assertEquals(2, positions.size());
    }

    @Test
    void shouldHandleManySpokes() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        for (int i = 1; i <= 10; i++) {
            String spoke = "spoke" + i;
            graph.addVertex(spoke);
            graph.addEdge("hub", spoke);
        }

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        assertEquals(11, positions.size());
        for (Point position : positions.values()) {
            assertTrue(position.x() >= 0);
            assertTrue(position.x() <= 1280);
            assertTrue(position.y() >= 0);
            assertTrue(position.y() <= 720);
        }
    }

    @Test
    void shouldDistributeSpokesEvenly() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addVertex("spoke4");
        graph.addEdge("hub", "spoke1");
        graph.addEdge("hub", "spoke2");
        graph.addEdge("hub", "spoke3");
        graph.addEdge("hub", "spoke4");

        SceneState sceneState = new SceneState(1280, 720, graph);
        Map<String, Point> positions = strategy.calculatePositions(graph, sceneState);

        Point hubPosition = positions.get("hub");
        Point spoke1 = positions.get("spoke1");
        Point spoke2 = positions.get("spoke2");

        double angle1 = Math.atan2(spoke1.y() - hubPosition.y(), spoke1.x() - hubPosition.x());
        double angle2 = Math.atan2(spoke2.y() - hubPosition.y(), spoke2.x() - hubPosition.x());
        double angleDiff = Math.abs(angle2 - angle1);
        
        assertTrue(angleDiff > 1.0);
    }

    @Test
    void shouldReturnCorrectStrategyName() {
        assertEquals("Hub and Spoke", strategy.getName());
    }
}

