package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.layout.AStarPathSolver;
import com.dev.explainor.genesis.layout.GridBuilder;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.layout.PathFindingCoordinator;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LayoutProperties.class)
public class GenesisLayoutAutoConfiguration {

    @Bean
    public StoryboardValidator storyboardValidator() {
        return new StoryboardValidator();
    }

    @Bean
    public GridBuilder gridBuilder(LayoutProperties properties) {
        return new GridBuilder(properties);
    }

    @Bean
    public PathFinder pathFinder(GridBuilder gridBuilder) {
        int explorationMargin = 50;
        int maxIterations = 10000;
        return new PathFindingCoordinator(gridBuilder, new AStarPathSolver(explorationMargin, maxIterations));
    }
}


