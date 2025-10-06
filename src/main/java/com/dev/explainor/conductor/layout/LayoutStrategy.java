package com.dev.explainor.conductor.layout;

import com.dev.explainor.conductor.service.SceneState;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

@Deprecated(since = "Genesis", forRemoval = true)
public interface LayoutStrategy {
    Map<String, Point> calculatePositions(Graph<String, DefaultEdge> graph, SceneState sceneState);
    String getName();
}

