package com.dev.explainor.conductor.layout;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PatternDetector {

    private static final Logger log = LoggerFactory.getLogger(PatternDetector.class);

    private final HubAndSpokeLayoutStrategy hubAndSpokeLayout;
    private final LinearChainLayoutStrategy linearChainLayout;
    private final HierarchicalLayoutStrategy hierarchicalLayout;
    private final GridLayoutStrategy gridLayout;

    public PatternDetector(
            HubAndSpokeLayoutStrategy hubAndSpokeLayout,
            LinearChainLayoutStrategy linearChainLayout,
            HierarchicalLayoutStrategy hierarchicalLayout,
            GridLayoutStrategy gridLayout) {
        this.hubAndSpokeLayout = hubAndSpokeLayout;
        this.linearChainLayout = linearChainLayout;
        this.hierarchicalLayout = hierarchicalLayout;
        this.gridLayout = gridLayout;
    }

    public LayoutStrategy detectPattern(Graph<String, DefaultEdge> graph) {
        if (graph.vertexSet().isEmpty()) {
            log.debug("Empty graph, using Grid layout");
            return gridLayout;
        }

        if (isTree(graph)) {
            log.info("Detected Tree/Hierarchical pattern");
            return hierarchicalLayout;
        }

        if (isLinearChain(graph)) {
            log.info("Detected Linear Chain pattern");
            return linearChainLayout;
        }

        if (isHubAndSpoke(graph)) {
            log.info("Detected Hub-and-Spoke pattern");
            return hubAndSpokeLayout;
        }

        log.debug("No specific pattern detected, using Grid layout");
        return gridLayout;
    }

    private boolean isHubAndSpoke(Graph<String, DefaultEdge> graph) {
        long highDegreeNodes = graph.vertexSet().stream()
                .filter(v -> graph.degreeOf(v) >= 3)
                .count();

        return highDegreeNodes == 1;
    }

    private boolean isLinearChain(Graph<String, DefaultEdge> graph) {
        if (graph.edgeSet().isEmpty()) {
            return false;
        }

        long chainNodes = graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) <= 1 && graph.outDegreeOf(v) <= 1)
                .count();

        return chainNodes == graph.vertexSet().size();
    }

    private boolean isTree(Graph<String, DefaultEdge> graph) {
        if (graph.edgeSet().isEmpty()) {
            return false;
        }

        long rootNodes = graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) == 0)
                .count();

        if (rootNodes != 1) {
            return false;
        }

        long branchNodes = graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) >= 2)
                .count();

        long intermediateNodes = graph.vertexSet().stream()
                .filter(v -> graph.inDegreeOf(v) >= 1 && graph.outDegreeOf(v) >= 1)
                .count();

        return branchNodes >= 1 && intermediateNodes >= 1;
    }
}

