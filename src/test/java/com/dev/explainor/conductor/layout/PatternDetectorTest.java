package com.dev.explainor.conductor.layout;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternDetectorTest {

    private PatternDetector detector;
    private HubAndSpokeLayoutStrategy hubAndSpokeLayout;
    private LinearChainLayoutStrategy linearChainLayout;
    private HierarchicalLayoutStrategy hierarchicalLayout;
    private GridLayoutStrategy gridLayout;

    @BeforeEach
    void setUp() {
        hubAndSpokeLayout = new HubAndSpokeLayoutStrategy();
        linearChainLayout = new LinearChainLayoutStrategy();
        hierarchicalLayout = new HierarchicalLayoutStrategy();
        gridLayout = new GridLayoutStrategy();
        
        detector = new PatternDetector(
            hubAndSpokeLayout,
            linearChainLayout,
            hierarchicalLayout,
            gridLayout
        );
    }

    @Test
    void shouldDetectHubAndSpokePattern() {
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

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(hubAndSpokeLayout, strategy);
    }

    @Test
    void shouldDetectLinearChainPattern() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addVertex("node4");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");
        graph.addEdge("node3", "node4");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(linearChainLayout, strategy);
    }

    @Test
    void shouldDetectTreePattern() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addVertex("grandchild1");
        graph.addVertex("grandchild2");
        graph.addEdge("root", "child1");
        graph.addEdge("root", "child2");
        graph.addEdge("child1", "grandchild1");
        graph.addEdge("child1", "grandchild2");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(hierarchicalLayout, strategy);
    }

    @Test
    void shouldFallbackToGridForEmptyGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(gridLayout, strategy);
    }

    @Test
    void shouldFallbackToGridForComplexGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addVertex("node4");
        graph.addEdge("node1", "node2");
        graph.addEdge("node1", "node3");
        graph.addEdge("node2", "node4");
        graph.addEdge("node3", "node4");
        graph.addEdge("node4", "node1");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(gridLayout, strategy);
    }

    @Test
    void shouldDetectHubAndSpokeWithIncomingEdges() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addEdge("spoke1", "hub");
        graph.addEdge("spoke2", "hub");
        graph.addEdge("spoke3", "hub");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(hubAndSpokeLayout, strategy);
    }

    @Test
    void shouldNotDetectHubAndSpokeWithTwoHubs() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub1");
        graph.addVertex("hub2");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addVertex("spoke4");
        graph.addEdge("hub1", "spoke1");
        graph.addEdge("hub1", "spoke2");
        graph.addEdge("hub2", "spoke3");
        graph.addEdge("hub2", "spoke4");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertNotEquals(hubAndSpokeLayout, strategy);
    }

    @Test
    void shouldDetectLinearChainWithSingleEdge() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addEdge("node1", "node2");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(linearChainLayout, strategy);
    }

    @Test
    void shouldNotDetectLinearChainWithBranch() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");
        graph.addVertex("node3");
        graph.addVertex("node4");
        graph.addEdge("node1", "node2");
        graph.addEdge("node2", "node3");
        graph.addEdge("node2", "node4");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertNotEquals(linearChainLayout, strategy);
    }

    @Test
    void shouldNotDetectLinearChainForGraphWithoutEdges() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");
        graph.addVertex("node2");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(gridLayout, strategy);
    }

    @Test
    void shouldNotDetectTreeWithMultipleRoots() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root1");
        graph.addVertex("root2");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addEdge("root1", "child1");
        graph.addEdge("root2", "child2");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertNotEquals(hierarchicalLayout, strategy);
    }

    @Test
    void shouldNotDetectTreeWithoutBranching() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        graph.addVertex("child");
        graph.addEdge("root", "child");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(linearChainLayout, strategy);
    }

    @Test
    void shouldDetectTreeWithDeepHierarchy() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("root");
        graph.addVertex("child1");
        graph.addVertex("child2");
        graph.addVertex("grandchild1");
        graph.addVertex("grandchild2");
        graph.addVertex("grandchild3");
        graph.addEdge("root", "child1");
        graph.addEdge("root", "child2");
        graph.addEdge("child2", "grandchild1");
        graph.addEdge("child2", "grandchild2");
        graph.addEdge("child2", "grandchild3");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(hierarchicalLayout, strategy);
    }

    @Test
    void shouldHandleSingleNodeGraph() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("node1");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(gridLayout, strategy);
    }

    @Test
    void shouldPrioritizeHubAndSpokeOverOtherPatterns() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("hub");
        graph.addVertex("spoke1");
        graph.addVertex("spoke2");
        graph.addVertex("spoke3");
        graph.addVertex("spoke4");
        graph.addEdge("hub", "spoke1");
        graph.addEdge("hub", "spoke2");
        graph.addEdge("hub", "spoke3");
        graph.addEdge("spoke1", "spoke4");

        LayoutStrategy strategy = detector.detectPattern(graph);

        assertEquals(hierarchicalLayout, strategy);
    }
}

