package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.domain.CreateEntityCommand;
import com.dev.explainor.conductor.domain.CreateEntityParams;
import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphBasedLayoutManagerTest {

    private GraphBasedLayoutManager layoutManager;
    private SceneState sceneState;
    private PatternDetector patternDetector;

    @BeforeEach
    void setUp() {
        GridLayoutStrategy gridLayout = new GridLayoutStrategy();
        HubAndSpokeLayoutStrategy hubAndSpokeLayout = new HubAndSpokeLayoutStrategy();
        LinearChainLayoutStrategy linearChainLayout = new LinearChainLayoutStrategy();
        HierarchicalLayoutStrategy hierarchicalLayout = new HierarchicalLayoutStrategy();
        
        patternDetector = new PatternDetector(hubAndSpokeLayout, linearChainLayout, hierarchicalLayout, gridLayout);
        layoutManager = new GraphBasedLayoutManager(patternDetector);
        
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        sceneState = new SceneState(1280, 720, graph);
    }

    @Test
    void shouldPositionSingleEntityInCenter() {
        CreateEntityCommand command = new CreateEntityCommand(
                "entity1",
                new CreateEntityParams("Entity 1", "server", null, null)
        );

        Point position = layoutManager.calculatePosition(command, sceneState);

        assertNotNull(position);
        assertTrue(position.x() >= 0 && position.x() <= 1280);
        assertTrue(position.y() >= 0 && position.y() <= 720);
    }

    @Test
    void shouldPositionMultipleEntitiesWithoutOverlap() {
        Graph<String, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
        graph.addVertex("entity1");
        graph.addVertex("entity2");
        graph.addVertex("entity3");
        
        SceneState stateWithGraph = new SceneState(1280, 720, graph);
        
        CreateEntityCommand command1 = new CreateEntityCommand(
                "entity1",
                new CreateEntityParams("Entity 1", "server", null, null)
        );
        CreateEntityCommand command2 = new CreateEntityCommand(
                "entity2",
                new CreateEntityParams("Entity 2", "database", null, null)
        );
        CreateEntityCommand command3 = new CreateEntityCommand(
                "entity3",
                new CreateEntityParams("Entity 3", "cloud", null, null)
        );

        Point pos1 = layoutManager.calculatePosition(command1, stateWithGraph);
        Point pos2 = layoutManager.calculatePosition(command2, stateWithGraph);
        Point pos3 = layoutManager.calculatePosition(command3, stateWithGraph);

        assertNotNull(pos1);
        assertNotNull(pos2);
        assertNotNull(pos3);

        assertNotEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
        assertNotEquals(pos2, pos3);
    }

    @Test
    void shouldRespectCanvasBounds() {
        for (int i = 0; i < 10; i++) {
            CreateEntityCommand command = new CreateEntityCommand(
                    "entity" + i,
                    new CreateEntityParams("Entity " + i, "server", null, null)
            );

            Point position = layoutManager.calculatePosition(command, sceneState);

            assertTrue(position.x() >= 0, "X position should be >= 0");
            assertTrue(position.x() <= 1280, "X position should be <= canvas width");
            assertTrue(position.y() >= 0, "Y position should be >= 0");
            assertTrue(position.y() <= 720, "Y position should be <= canvas height");
        }
    }

    @Test
    void shouldCachePositionsAfterFirstCalculation() {
        CreateEntityCommand command1 = new CreateEntityCommand(
                "entity1",
                new CreateEntityParams("Entity 1", "server", null, null)
        );
        CreateEntityCommand command2 = new CreateEntityCommand(
                "entity2",
                new CreateEntityParams("Entity 2", "database", null, null)
        );

        Point pos1_first = layoutManager.calculatePosition(command1, sceneState);
        Point pos1_second = layoutManager.calculatePosition(command1, sceneState);

        assertEquals(pos1_first, pos1_second);
    }

    @Test
    void shouldResetStateWhenCallingReset() {
        CreateEntityCommand command = new CreateEntityCommand(
                "entity1",
                new CreateEntityParams("Entity 1", "server", null, null)
        );

        Point pos1 = layoutManager.calculatePosition(command, sceneState);

        layoutManager.reset();

        Graph<String, DefaultEdge> newGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        SceneState newSceneState = new SceneState(1280, 720, newGraph);
        Point pos2 = layoutManager.calculatePosition(command, newSceneState);

        assertNotNull(pos1);
        assertNotNull(pos2);
    }

    @Test
    void shouldHandleEmptySceneState() {
        Graph<String, DefaultEdge> emptyGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
        SceneState emptyState = new SceneState(1280, 720, emptyGraph);

        CreateEntityCommand command = new CreateEntityCommand(
                "entity1",
                new CreateEntityParams("Entity 1", "server", null, null)
        );

        Point position = layoutManager.calculatePosition(command, emptyState);

        assertNotNull(position);
        assertTrue(position.x() >= 0 && position.x() <= 1280);
        assertTrue(position.y() >= 0 && position.y() <= 720);
    }

    @Test
    void shouldDistributeEntitiesInGrid() {
        CreateEntityCommand command1 = new CreateEntityCommand("e1", new CreateEntityParams("E1", null, null, null));
        CreateEntityCommand command2 = new CreateEntityCommand("e2", new CreateEntityParams("E2", null, null, null));
        CreateEntityCommand command3 = new CreateEntityCommand("e3", new CreateEntityParams("E3", null, null, null));
        CreateEntityCommand command4 = new CreateEntityCommand("e4", new CreateEntityParams("E4", null, null, null));

        Point p1 = layoutManager.calculatePosition(command1, sceneState);
        Point p2 = layoutManager.calculatePosition(command2, sceneState);
        Point p3 = layoutManager.calculatePosition(command3, sceneState);
        Point p4 = layoutManager.calculatePosition(command4, sceneState);

        assertNotNull(p1);
        assertNotNull(p2);
        assertNotNull(p3);
        assertNotNull(p4);

        assertTrue(p1.x() < 1280 && p1.y() < 720);
        assertTrue(p2.x() < 1280 && p2.y() < 720);
        assertTrue(p3.x() < 1280 && p3.y() < 720);
        assertTrue(p4.x() < 1280 && p4.y() < 720);
    }
}

