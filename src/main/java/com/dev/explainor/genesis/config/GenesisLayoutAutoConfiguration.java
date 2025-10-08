package com.dev.explainor.genesis.config;

import com.dev.explainor.genesis.layout.DummyLayoutManager;
import com.dev.explainor.genesis.layout.GraphBasedLayoutManager;
import com.dev.explainor.genesis.layout.LayoutManager;
import com.dev.explainor.genesis.layout.PathFinder;
import com.dev.explainor.genesis.layout.OrthogonalPathFinder;
import com.dev.explainor.genesis.validation.StoryboardValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Configuration
    @ConditionalOnProperty(prefix = "layout", name = "algorithm", havingValue = "graph")
    static class GraphLayoutConfiguration {
        @Bean(name = "genesisPathFinder")
        PathFinder pathFinder(LayoutProperties props) {
            return new OrthogonalPathFinder(props);
        }

        @Bean(name = "genesisLayoutManager")
        LayoutManager layoutManager(PathFinder pathFinder, LayoutProperties props) {
            return new GraphBasedLayoutManager(pathFinder, props);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "layout", name = "algorithm", havingValue = "dummy", matchIfMissing = true)
    static class DummyLayoutConfiguration {
        @Bean(name = "genesisLayoutManager")
        @ConditionalOnMissingBean(LayoutManager.class)
        LayoutManager layoutManager(LayoutProperties props) {
            return new DummyLayoutManager();
        }
    }
}


